package com.chinacloud.utils;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.joda.time.Hours;
import org.joda.time.Seconds;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Util class for parsing cron expression and calculating its operation time.
 * Keep in mind that the skew time for execution time calculation should be less
 * than or equal to half of the interval between two executions.
 * <p>
 * 
 * @author tao
 *
 */
public class CronExpParser {

	// CronDefinition instance for QUARTZ
	private static CronDefinition CRONDEFINITION = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
	private static DateTimeFormatter FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
	private static int ONE_HOUR = 3600;

	/**
	 * Parse a cron expression and calculate its operation time between the
	 * start and end time specified by the caller. The time interval between
	 * start and end must be less than or equal to 720 hours (30 days).
	 * <p>
	 * 
	 * @param cronExp
	 *            Cron Expression
	 * @param start
	 *            Start time
	 * @param end
	 *            End time
	 * @return Null if end time is before start time or if the next execution is
	 *         after end time. Or a list of DateTime instances which stands for
	 *         executions time points between start and end.
	 * @throws IllegalArgumentException
	 *             if the time interval is longer than 720 hours
	 */
	public List<DateTime> parseSoloCronExp(String cronExp, String start, String end) throws IllegalArgumentException {

		DateTime startTime = DateTime.parse(start, FORMATTER);
		DateTime endTime = DateTime.parse(end, FORMATTER);
		if (endTime.isBefore(startTime)) {
			return null;
		}
		// get hours interval between start and end.
		int hours = Hours.hoursBetween(startTime, endTime).getHours();
		if (hours > 720)
			throw new IllegalArgumentException("This time interval is too long (longer than 1 month)!!");

		ExecutionTime execTime = parseCronExpToExecTime(cronExp);
		DateTime nextExecTime = execTime.nextExecution(startTime);

		// if the next execution is after end time then return null.
		if (nextExecTime.isAfter(endTime)) {
			return null;
		}

		List<DateTime> res = Lists.newArrayList();
		// add in the first execution time, bug point created
		res.add(nextExecTime);
		nextExecTime = recursionOfExecTime(execTime, nextExecTime, endTime, res);
		return res;
	}

	/**
	 * Variance of parseSoloCronExp(String cronStr, String start, String end),
	 * instead of passing Strings, DateTime was used.
	 * 
	 * @see com.github.ltsopensource.admin.chinacloud.util.CronExpParser
	 */
	public List<DateTime> parseSoloCronExp(String cronExp, DateTime startTime, DateTime endTime)
			throws IllegalArgumentException {

		if (endTime.isBefore(startTime)) {
			return null;
		}
		// get hours interval between start and end.
		int hours = Hours.hoursBetween(startTime, endTime).getHours();
		if (hours > 720)
			throw new IllegalArgumentException("This time interval is too long (longer than 1 month)!!");

		ExecutionTime execTime = parseCronExpToExecTime(cronExp);
		DateTime nextExecTime = execTime.nextExecution(startTime);

		// if the next execution is after end time then return null.
		if (nextExecTime.isAfter(endTime)) {
			return null;
		}

		List<DateTime> res = Lists.newArrayList();
		// add in the first execution time, bug point created
		res.add(nextExecTime);
		nextExecTime = recursionOfExecTime(execTime, nextExecTime, endTime, res);
		return res;
	}

	/**
	 * Cron jobs statistics for the next 24hours. See return statement.
	 * 
	 * @param mapCrons
	 *            Task id mapping to cron expression
	 * @return a list of String list, contains next 24 hours' tasks
	 */
	public List<List<String>> parseCronLs4OneDay(Map<String, String> mapCrons, String currentTime) {

		Preconditions.checkArgument(mapCrons != null, "no crons !");
		Preconditions.checkArgument(currentTime != null, "no currentTime !");
		Map<String, List<String>> retMap = Maps.newHashMap();
		List<DateTime> scales = Lists.newArrayList();

		org.joda.time.format.DateTimeFormatter formatter =
				         DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
		DateTime startTime = formatter.parseDateTime(currentTime);
		//DateTime startTime = DateTime.now();
		// this one is for result map initialization
		// DateTime endTime = DateTime.now();
		DateTime endTime = startTime;
		// initialize result map
		for (int i = 0; i < 24; i++) {
			retMap.put(endTime.toString(FORMATTER), new ArrayList<String>());
			scales.add(endTime);
			endTime = endTime.plusHours(1);
		}

		Set<Entry<String, String>> entrySet = mapCrons.entrySet();
		for (Entry<String, String> entry : entrySet) {
			List<DateTime> execTimes = parseSoloCronExp(entry.getValue(), startTime, endTime);
			// the following logic definitely needs optimization
			if (null != execTimes) {
				for (DateTime execTime : execTimes) {
					for (DateTime scale : scales) {
						if (Seconds.secondsBetween(scale, execTime).getSeconds() < ONE_HOUR) {
							retMap.get(scale.toString(FORMATTER)).add(entry.getKey());
							break;
						}
					}
				}
			}
		}

		List<List<String>> rets = Lists.newArrayList();
		for (DateTime scale : scales) {
			rets.add(retMap.get(scale.toString(FORMATTER)));
		}
		return rets;
	}

	/**
	 * Util method for parsing a QUARTZ cron exporession.
	 * 
	 * @param cronExp
	 *            cron expression
	 * @return instance of ExecutionTime
	 */
	public static ExecutionTime parseCronExpToExecTime(String cronExp) {

		CronParser parser = new CronParser(CRONDEFINITION);
		Cron quartzCron = parser.parse(cronExp);

		return ExecutionTime.forCron(quartzCron);
	}

	/**
	 * Buggie point!!!!!! Recursion method handles execution time. skewing time
	 * for calculation of cron operation time, here it is 1 second, the
	 * operation more frequently than 1 time per second will be out of this
	 * method's scope, maybe even cron expression's scope.
	 * 
	 * @param execTime
	 *            ExecutionTime instance
	 * @param start
	 *            Start time point for each recursion
	 * @param end
	 *            end time point for each recursion
	 * @param res
	 *            result list contains all executions
	 * @return
	 */
	private DateTime recursionOfExecTime(ExecutionTime execTime, DateTime start, DateTime end, List<DateTime> res) {

		// skew the start time to avoid repeating execution time (caused by
		// cron-util's behavior, comparison by millisecond)
		start = start.plusSeconds(1);
		start = execTime.nextExecution(start);
		if (start.isBefore(end)) {
			res.add(start);
			start = recursionOfExecTime(execTime, start, end, res);
		}
		return start;
	}

}
