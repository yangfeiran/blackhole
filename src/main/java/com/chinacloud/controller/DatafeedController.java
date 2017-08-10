package com.chinacloud.controller;

import com.alibaba.fastjson.JSONObject;
import com.chinacloud.bean.DatafeedJobBean;
import com.chinacloud.bean.ResultInfo;
import com.chinacloud.config.Datafeed;
import com.chinacloud.service.JobManager;
import com.chinacloud.utils.AES;
import com.chinacloud.utils.CronExpParser;
import com.chinacloud.utils.FtpUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.thinkbiganalytics.nifi.rest.client.NifiComponentNotFoundException;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;
import org.apache.nifi.web.api.dto.ProcessGroupDTO;
import org.apache.nifi.web.api.dto.TemplateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/20.
 */
@RestController
@RequestMapping("/v1/datafeed")
public class DatafeedController extends BaseController {
    private static final Logger log = Logger.getLogger(DatafeedController.class);
    @Autowired
    Datafeed datafeed;
    @Autowired
    private JobManager jobManager;


    static final List<String> SCHEMA_CHECK = Lists.newArrayList("ADD", "ADMIN", "AFTER", "ALL", "ALTER", "ANALYZE", "AND", "ARCHIVE", "ARRAY", "AS", "ASC", "AUTHORIZATION", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BOOLEAN", "BOTH", "BUCKET", "BUCKETS", "BY", "CASCADE", "CASE", "CAST", "CHANGE", "CHAR", "CLUSTER", "CLUSTERED", "CLUSTERSTATUS", "COLLECTION", "COLUMN", "COLUMNS", "COMMENT", "COMPACT", "COMPACTIONS", "COMPUTE", "CONCATENATE", "CONF", "CONTINUE", "CREATE", "CROSS", "CUBE", "CURRENT", "CURRENT_DATE", "CURRENT_TIMESTAMP", "CURSOR", "DATA", "DATABASE", "DATABASES", "DATE", "DATETIME", "DAY", "DBPROPERTIES", "DECIMAL", "DEFERRED", "DEFINED", "DELETE", "DELIMITED", "DEPENDENCY", "DESC", "DESCRIBE", "DIRECTORIES", "DIRECTORY", "DISABLE", "DISTINCT", "DISTRIBUTE", "DOUBLE", "DROP", "ELEM_TYPE", "ELSE", "ENABLE", "END", "ESCAPED", "EXCHANGE", "EXCLUSIVE", "EXISTS", "EXPLAIN", "EXPORT", "EXTENDED", "EXTERNAL", "FALSE", "FETCH", "FIELDS", "FILE", "FILEFORMAT", "FIRST", "FLOAT", "FOLLOWING", "FOR", "FORMAT", "FORMATTED", "FROM", "FULL", "FUNCTION", "FUNCTIONS", "GRANT", "GROUP", "GROUPING", "HAVING", "HOLD_DDLTIME", "HOUR", "IDXPROPERTIES", "IF", "IGNORE", "IMPORT", "IN", "INDEX", "INDEXES", "INNER", "INPATH", "INPUTDRIVER", "INPUTFORMAT", "INSERT", "INT", "INTERSECT", "INTERVAL", "INTO", "IS", "ITEMS", "JAR", "JOIN", "KEYS", "KEY_TYPE", "LATERAL", "LEFT", "LESS", "LIKE", "LIMIT", "LINES", "LOAD", "LOCAL", "LOCATION", "LOCK", "LOCKS", "LOGICAL", "LONG", "MACRO", "MAP", "MAPJOIN", "MATERIALIZED", "MINUS", "MINUTE", "MONTH", "MORE", "MSCK", "NONE", "NOSCAN", "NOT", "NO_DROP", "NULL", "OF", "OFFLINE", "ON", "OPTION", "OR", "ORDER", "OUT", "OUTER", "OUTPUTDRIVER", "OUTPUTFORMAT", "OVER", "OVERWRITE", "OWNER", "PARTIALSCAN", "PARTITION", "PARTITIONED", "PARTITIONS", "PERCENT", "PLUS", "PRECEDING", "PRESERVE", "PRETTY", "PRINCIPALS", "PROCEDURE", "PROTECTION", "PURGE", "RANGE", "READ", "READONLY", "READS", "REBUILD", "RECORDREADER", "RECORDWRITER", "REDUCE", "REGEXP", "RELOAD", "RENAME", "REPAIR", "REPLACE", "RESTRICT", "REVOKE", "REWRITE", "RIGHT", "RLIKE", "ROLE", "ROLES", "ROLLUP", "ROW", "ROWS", "SCHEMA", "SCHEMAS", "SECOND", "SELECT", "SEMI", "SERDE", "SERDEPROPERTIES", "SERVER", "SET", "SETS", "SHARED", "SHOW", "SHOW_DATABASE", "SKEWED", "SMALLINT", "SORT", "SORTED", "SSL", "STATISTICS", "STORED", "STREAMTABLE", "STRING", "STRUCT", "TABLE", "TABLES", "TABLESAMPLE", "TBLPROPERTIES", "TEMPORARY", "TERMINATED", "THEN", "TIMESTAMP", "TINYINT", "TO", "TOUCH", "TRANSACTIONS", "TRANSFORM", "TRIGGER", "TRUE", "TRUNCATE", "UNARCHIVE", "UNBOUNDED", "UNDO", "UNION", "UNIONTYPE", "UNIQUEJOIN", "UNLOCK", "UNSET", "UNSIGNED", "UPDATE", "URI", "USE", "USER", "USING", "UTC", "UTCTIMESTAMP", "VALUES", "VALUE_TYPE", "VARCHAR", "VIEW", "WHEN", "WHERE", "WHILE", "WINDOW", "WITH", "YEAR");

    @RequestMapping(value = "/templates", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getTemplates() {
        ResultInfo info = new ResultInfo();
        info.setResult(jobManager.getTemplates());
        info.setCode(HttpStatus.OK.value());
        info.setStatus("success");
        return info;
    }

    @RequestMapping(value = "/job", method = RequestMethod.POST)
    @ResponseBody
    public ResultInfo createJob(@RequestBody DatafeedJobBean datafeedJobBean) {
        ResultInfo info = new ResultInfo();
        ProcessGroupDTO groupDTO = jobManager.createProcessGroup(datafeedJobBean.getJobName());
        String password = (String) ((Map) datafeedJobBean.getConfig().get("input")).get("password");
        try {
            String password_decode = AES.Decrypt(password, datafeed.getSecret());
            log.info("password_decode: " + password_decode);
            ((Map) datafeedJobBean.getConfig().get("input")).put("password", password_decode);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.info("password can not be decoded, " + e.getMessage());
            response.setStatus(HttpStatus.CONFLICT.value());
            info.setCode("PasswordCannotParse");
            info.setMsg("Password Can not Parse");
            return info;
        }
        List<TemplateDTO> templates = jobManager.getTemplates();
        if(((String) ((Map<String, Object>) datafeedJobBean.getConfig().get("output")).get("type")).equalsIgnoreCase("hive")) {
            for (TemplateDTO templateDTO : templates) {
                if (templateDTO.getName().indexOf(((String) ((Map<String, Object>) datafeedJobBean.getConfig().get("output")).get("targetType"))) >= 0)
                    datafeedJobBean.setTemplateId(templateDTO.getId());
            }
        }else if(((String) ((Map<String, Object>) datafeedJobBean.getConfig().get("output")).get("type")).equalsIgnoreCase("hdfs")){
            for (TemplateDTO templateDTO : templates) {
                if (templateDTO.getName().indexOf("csv") >= 0)
                    datafeedJobBean.setTemplateId(templateDTO.getId());
            }
        }
        log.info("jobname: " + datafeedJobBean.getJobName() + ", templateId: " + datafeedJobBean.getTemplateId() + ", jobInfo: " + datafeedJobBean.getJobInfo() + ", labels: " + datafeedJobBean.getLabels() + ", config: " + datafeedJobBean.getConfig().toString() + ", cron: " + datafeedJobBean.getCron());
        boolean result = jobManager.createTemplateInstance(groupDTO.getId(), datafeedJobBean.getTemplateId(), datafeedJobBean.getConfig());
        Map<String,String> cron = Maps.newHashMap();
        cron.put("cron",datafeedJobBean.getCron());
        updateJobScheduler(groupDTO.getId(),cron);
        log.info("result: " + result);
        if (result) {
            jobManager.saveJob(datafeedJobBean.getJobName(), groupDTO.getId(), datafeedJobBean.getTemplateId(), datafeedJobBean.getJobInfo(), datafeedJobBean.getLabels(), datafeedJobBean.getCron(), datafeedJobBean.getConfig());
            info.setResult(groupDTO);
            info.setCode(HttpStatus.CREATED.value());
            info.setStatus("success");
            info.setMsg("Successful");
            response.setStatus(HttpStatus.CREATED.value());
            return info;
        }
        info.setCode("InternalServerError");
        info.setMsg("Internal server error");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return info;
    }

    @RequestMapping(value = "/jobs/{jobId}/status", method = RequestMethod.PUT)
    @ResponseBody
    public ResultInfo updateJobStatus(@PathVariable("jobId") String jobId, @RequestBody Map<String, String> action) {
        ResultInfo info = new ResultInfo();
        boolean result = false;
        if (action.get("action").equalsIgnoreCase(jobManager.getJobStatus(jobId))) {
            info.setCode("JobAlreadyInStatus");
            info.setMsg("Job id " + jobId + " is already in status");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return info;
        }
        try {
            result = jobManager.updateProcessGroupStatus(jobId, action.get("action"));
        } catch (NifiComponentNotFoundException e) {
            info.setMsg("Job id " + jobId + " does not exist");
            info.setCode("JobIdNotExist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return info;
        }
        if (result) {
            jobManager.updateJobStatus(jobId, action.get("action"));
            info.setMsg("Successful");
            info.setCode(HttpStatus.OK.value());
            info.setStatus("success");
            response.setStatus(HttpStatus.OK.value());
            return info;
        }
        info.setCode("InternalServerError");
        info.setMsg("Internal server error");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return info;
    }

    @RequestMapping(value = "/jobs/{jobId}/scheduler", method = RequestMethod.PUT)
    @ResponseBody
    public ResultInfo updateJobScheduler(@PathVariable("jobId") String jobId, @RequestBody Map<String, String> cron) {
        ResultInfo info = new ResultInfo();
        boolean result = false;
        try {
            CronExpParser.parseCronExpToExecTime(cron.get("cron"));
        } catch (IllegalArgumentException e) {
            info.setMsg("Parameter cron " + cron.get("cron") + "  is invalid");
            info.setCode("CronExpressionInvalid");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return info;
        }
        try {
            result = jobManager.updateProcessGroupScheduler(jobId, cron.get("cron"));
        } catch (NifiComponentNotFoundException e) {
            info.setMsg("Job id " + jobId + " does not exist");
            info.setCode("JobIdNotExist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return info;
        }
        if (result) {
            jobManager.updateJobScheduler(jobId, cron.get("cron"));
            info.setMsg("Successful");
            info.setCode(HttpStatus.OK.value());
            info.setStatus("success");
            response.setStatus(HttpStatus.OK.value());
            return info;
        }
        info.setMsg("Internal server error");
        info.setCode("InternalServerError");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return info;
    }

    @RequestMapping(value = "/jobs/{jobId}/info", method = RequestMethod.PUT)
    @ResponseBody
    public ResultInfo updateJobInfo(@PathVariable("jobId") String jobId, @RequestBody Map<String, Object> updateInfo) {
        ResultInfo info = new ResultInfo();
//        boolean result = jobManager.updateProcessGroupInfo(jobId, (String) updateInfo.get("jobInfo"), (List<String>) updateInfo.get("lables"));
        String s = ((List<String>) updateInfo.get("lables")).toString();
        System.out.println(s);
        if (jobManager.findJobIfExist(jobId) != null) {
            jobManager.updateJobInfo(jobId, (String) updateInfo.get("jobInfo"), /*Arrays.toString((*/((List<String>) updateInfo.get("lables")).toString()/*).toArray())*/);
            info.setMsg("Successful");
            info.setCode(HttpStatus.OK.value());
            info.setStatus("success");
            response.setStatus(HttpStatus.OK.value());
            return info;
        }
        info.setMsg("Job id " + jobId + " does not exist");
        info.setCode("JobIdNotExist");
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return info;
    }

    @RequestMapping(value = "/jobs/{jobId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResultInfo deleteJob(@PathVariable("jobId") String jobId) {
        ResultInfo info = new ResultInfo();
        boolean result = false;
        try {
            jobManager.updateJobStatus(jobId, "STOPPED");
            result = jobManager.deleteProcessGroup(jobId);
        } catch (NifiComponentNotFoundException e) {
            info.setMsg("Job id " + jobId + " does not exist");
            info.setCode("JobIdNotExist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return info;
        }
        if (result) {
            jobManager.deleteJob(jobId);
            info.setMsg("Successful");
            info.setCode(HttpStatus.OK.value());
            info.setStatus("success");
            response.setStatus(HttpStatus.OK.value());
            return info;
        }
        info.setMsg("Internal server error");
        info.setCode("InternalServerError");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        return info;
    }

    @RequestMapping(value = "/jobs", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getAllJobs(@RequestParam("query") String query, @RequestParam("page") int page, @RequestParam("size") int size) {
        ResultInfo info = new ResultInfo();
        List<Map<String, Object>> result = jobManager.getAllProcessGroups(query);
        info.setResult(result.subList(size * (page - 1), Math.min(result.size(), size * page)));
        info.setCode(HttpStatus.OK.value());
        info.setStatus("success");
        info.setMsg(String.valueOf(result.size()));
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

    @RequestMapping(value = "/jobs/{jobId}", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getJob(@PathVariable("jobId") String jobId) {
        ResultInfo info = new ResultInfo();
        Map<String, Object> result = Maps.newHashMap();
        try {
            result = jobManager.getProcessGroupById(jobId);
        } catch (NifiComponentNotFoundException e) {
            info.setMsg("Job id " + jobId + " does not exist");
            info.setCode("JobIdNotExist");
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return info;
        }
        info.setResult(result);
        info.setCode(HttpStatus.OK.value());
        info.setStatus("success");
        info.setMsg("Successful");
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

    @RequestMapping(value = "/jobs/nexttimes", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getAllJobsNexttimes(@RequestParam("currentTime") String currentTime) {
        ResultInfo info = new ResultInfo();
        List<List<String>> result = jobManager.getAllProcessGroupsNexttimes(currentTime);
        info.setResult(result);
        info.setCode(HttpStatus.OK.value());
        response.setStatus(HttpStatus.OK.value());
        info.setStatus("success");
        info.setMsg("Successful");
        return info;

    }

    @RequestMapping(value = "/healthCheck", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getHealthCheck() {
        ResultInfo info = new ResultInfo();
        info.setMsg("Datafeed");
        info.setCode(HttpStatus.NO_CONTENT.value());
        info.setStatus("success");
        return info;
    }

    @RequestMapping(value = "/jobNameCheck", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getJobNameCheck(@RequestParam("jobName") String jobName) {
        ResultInfo info = new ResultInfo();
        if (jobManager.findJobNameIfExist(jobName)) {
            info.setMsg("Job name " + jobName + " not available");
            response.setStatus(HttpStatus.CONFLICT.value());
            info.setCode("JobNameNotAvailable");
        } else {
            info.setMsg("200");
            info.setStatus("ok");
            response.setStatus(HttpStatus.OK.value());
            info.setCode(HttpStatus.OK.value());
            return info;
        }
        return info;
    }

    @RequestMapping(value = "/connectionIDCheck", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo checkDataSource(@RequestParam("connectionID") String connectionID) {
        ResultInfo info = new ResultInfo();
        if (jobManager.findConnectionIDIfExist(connectionID)) {
            info.setCode(HttpStatus.NOT_MODIFIED.value());
            info.setMsg("exist");
        } else {
            info.setCode(HttpStatus.OK.value());
            info.setMsg("ok");
        }
        return info;
    }

    @RequestMapping(value = "/allTotalCountByOrder", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getAllTotalCountByOrder() {
        ResultInfo info = new ResultInfo();
        List<Map<String, List<Map<String, String>>>> allTotalCountByOrder = jobManager.getAllTotalCountByOrder();
        info.setStatus("success");
        info.setCode(HttpStatus.OK.value());
        info.setResult(allTotalCountByOrder);
        info.setMsg("Successful");
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

    @RequestMapping(value = "/allTotalCountTogether", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getAllTotalCountTogether() {
        ResultInfo info = new ResultInfo();
        Map<String, String> allTotalCountTogether = jobManager.getAllTotalCountTogether();
        info.setStatus("success");
        info.setCode(HttpStatus.OK.value());
        info.setResult(allTotalCountTogether);
        info.setMsg("Successful");
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

    public static void main(String[] args) {
        DatafeedController datafeedController = new DatafeedController();
//        String s = "/hom";
//        String s = "/home";
//        String s = "/home/";
//        String s = "/";
        String s = "";
        ResultInfo fc = datafeedController.checkFtpPath("172.16.50.22", "123", "fc", 21, s);
//        System.out.println(fc);
    }

    @RequestMapping(value = "/checkFtpPath", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo checkFtpPath(@RequestParam("host") String host, @RequestParam("password") String password, @RequestParam("username") String username, @RequestParam("port") int port, @RequestParam("path") String path) {
        ResultInfo info = new ResultInfo();
        log.info("path: " + path);
        try {
            password = AES.Decrypt(password, datafeed.getSecret());
            log.info("password_decode: " + password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            log.info("password can not be decoded, " + e.getMessage());
            response.setStatus(HttpStatus.CONFLICT.value());
            info.setCode("PasswordCannotParse");
            info.setMsg("Password Can not Parse");
            return info;
        }
        FTPClient ftpClient = null;
        try {
            ftpClient = FtpUtils.getFTPClient(host, password, username, port);
        }catch (Exception e){
            info.setStatus("fail");
            info.setMsg("FTP Connect Fail");
            info.setCode("FTPConnectFail");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return info;
        }
        String[] array = path.split("/");
        log.info("array: " + Arrays.toString(array));
        String front = "/";
        String back = "";
        List<String> result_list = Lists.newArrayList();
        if (!path.endsWith("/")) {
            back = array.length - 1 > 0 ? array[array.length - 1] : "";
        }
        log.info("back path: " + back);
        if (!path.endsWith("/")) {
            for (int i = 0; i < array.length - 1; i++) {
                front += "/" + array[i];
            }
        } else {
            for (int i = 0; i < array.length; i++) {
                front += "/" + array[i];
            }
        }
        log.info("front path: " + front);
        List<String> path_list = null;
        try {
            path_list = FtpUtils.checkFtpPath(ftpClient, front);
            FtpUtils.closeServer(ftpClient);
        } catch (IOException e) {
            info.setStatus("fail");
            info.setMsg("FTP Connect Fail");
            info.setCode("FTPConnectFail");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return info;
        }
        log.info("path list: " + path_list);
        for (String one_path : path_list) {
            if (back.isEmpty() || one_path.startsWith(back)) {
                result_list.add((front + "/" + one_path).replaceAll("//", "/").replaceAll("//", "/"));
            }
        }

        log.info("result list: " + result_list);
        info.setResult(result_list);
        info.setStatus("success");
        info.setCode(HttpStatus.OK.value());
        info.setMsg("Successful");
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

    @RequestMapping(value = "/checkSchema", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo checkSchema(@RequestParam("schema") String schema) {
        ResultInfo info = new ResultInfo();
        Map<String, String> map = (Map) JSONObject.parse(schema);
        for (String key : map.keySet()) {
            if (SCHEMA_CHECK.contains(key.toUpperCase())) {
                info.setStatus("illegal");
                info.setMsg("Schema has error");
                info.setCode("SchemaError");
                response.setStatus(HttpStatus.CONFLICT.value());
                return info;
            }
        }
        info.setStatus("success");
        info.setCode(HttpStatus.OK.value());
        info.setMsg("Successful");
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

    @RequestMapping(value = "/getHDFSPath", method = RequestMethod.GET)
    @ResponseBody
    public ResultInfo getHDFSPath(@RequestParam("jobName") String jobName, @RequestParam("database") String database, @RequestParam("tablename") String tablename, @RequestParam("type") String type) {
        ResultInfo info = new ResultInfo();
        String path = jobManager.getPath(type, database, tablename, jobName);
        Map<String, String> result = Maps.newHashMap();
        result.put("path", path);
        info.setResult(result);
        info.setStatus("success");
        info.setCode(HttpStatus.OK.value());
        info.setMsg("Successful");
        response.setStatus(HttpStatus.OK.value());
        return info;
    }

}
