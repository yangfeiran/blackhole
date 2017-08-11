package com.chinacloud.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinacloud.dao.JobDao;
import com.chinacloud.dao.ProcessorDao;
import com.chinacloud.model.Job;
import com.chinacloud.model.Processor;
import com.chinacloud.process.CheckTotalProcess;
import com.chinacloud.utils.CronExpParser;
import com.chinacloud.utils.HDFSUtil;
import com.chinacloud.utils.HiveUtils;
import com.chinacloud.utils.MapBeanConvertor;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.thinkbiganalytics.nifi.rest.client.NiFiComponentState;
import com.thinkbiganalytics.nifi.rest.client.NifiComponentNotFoundException;
import com.thinkbiganalytics.nifi.rest.support.NifiConstants;
import com.thinkbiganalytics.nifi.rest.support.NifiFeedConstants;
import com.thinkbiganalytics.nifi.v1.rest.client.NiFiRestClientV1;
import org.apache.log4j.Logger;
import org.apache.nifi.web.api.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class JobManager {
    private static final Logger log = Logger.getLogger(JobManager.class);

    @Autowired
    private NiFiRestClientV1 niFiRestClientV1;

    @Autowired
    private CheckTotalProcess checkTotalProcess;

    @Autowired
    HiveUtils hiveUtils;
    @Autowired
    HDFSUtil hdfsUtil;

    @Autowired
    private ProcessorDao processorDao;

    @Autowired
    private JobDao jobDao;


    public List<TemplateDTO> getTemplates() {
        Set<TemplateDTO> result = niFiRestClientV1.templates().findAll();
        Iterator<TemplateDTO> iterator = result.iterator();
        List<TemplateDTO> templateList = Lists.newArrayList();
        while (iterator.hasNext()) {
            TemplateDTO template = iterator.next();
            templateList.add(template);
            log.info("Template: " + template.getName() + " is presented to the user");
        }
        return templateList;
    }

    public ProcessGroupDTO createProcessGroup(String processGroupName) {
        ProcessGroupDTO processGroupDTO = niFiRestClientV1.processGroups().findRoot();
        ProcessGroupDTO groupDTO = niFiRestClientV1.processGroups().create(processGroupDTO.getId(), processGroupName);
        log.info("A new process group is created: " + groupDTO.getName());
        return groupDTO;
    }

    public boolean createTemplateInstance(String groupId, String templateId, Map<String, Object> config) {
        Map<String, String> nifiInputBean = (Map<String, String>) config.get("input");
        Map<String, String> nifiOutputBean = (Map<String, String>) config.get("output");
        FlowSnippetDTO flowSnippetDTO = niFiRestClientV1.processGroups().instantiateTemplate(groupId, templateId);
        Set<ProcessorDTO> processors = flowSnippetDTO.getProcessors();
        Iterator<ProcessorDTO> processorDTOIterator = processors.iterator();
        while (processorDTOIterator.hasNext()) {
            ProcessorDTO processorDTO = processorDTOIterator.next();
            log.info("processor name: " + processorDTO.getName());
            Map<String, String> properties = updateProcessorConfig(processorDTO, nifiInputBean, nifiOutputBean);
            processorDTO.getConfig().setProperties(properties);
            niFiRestClientV1.processors().update(processorDTO);
            Processor processor = new Processor();
            processor.setJobId(groupId);
            processor.setConfig(JSONObject.toJSONString(config));
            processor.setProcessorId(processorDTO.getId());
            processorDao.save(processor);
        }
        if (nifiOutputBean.get("type").equalsIgnoreCase("hive")) {
            return hiveUtils.createHiveTable(nifiOutputBean.get("database"), nifiOutputBean.get("table"), JSON.toJSONString(nifiOutputBean.get("schema")), nifiOutputBean.get("path"), nifiOutputBean.get("targetType"), nifiOutputBean.get("targetFile"));
        } else if (nifiOutputBean.get("type").equalsIgnoreCase("hdfs")) {
            return true;
        }
//        ProcessorDTO processorDTO = niFiRestClientV1.processors().findById(groupId, processor1_id)
//                .orElseThrow(() -> new NifiComponentNotFoundException(processor1_id, NifiConstants.NIFI_COMPONENT_TYPE.PROCESSOR, null));
        return false;

    }

    private Map<String, String> updateProcessorConfig(ProcessorDTO processor, Map<String, String> nifiInputBean, Map<String, String> nifiOutputBean) {
        Map<String, String> properties = processor.getConfig().getProperties();
        if (processor.getName().equalsIgnoreCase("ListFTP")) {
            properties.put("Hostname", nifiInputBean.get("hostname"));
            properties.put("Port", nifiInputBean.get("port"));
            properties.put("Username", nifiInputBean.get("username"));
            properties.put("Password", nifiInputBean.get("password"));
            properties.put("Remote Path", nifiInputBean.get("remotePath"));
        } else if (processor.getName().equalsIgnoreCase("FetchFTP")) {
            properties.put("Hostname", nifiInputBean.get("hostname"));
            properties.put("Port", nifiInputBean.get("port"));
            properties.put("Username", nifiInputBean.get("username"));
            properties.put("Password", nifiInputBean.get("password"));
        } else if (processor.getName().equalsIgnoreCase("PutHDFS")) {
            properties.put("Directory", nifiOutputBean.get("path"));
        } else if (processor.getName().equalsIgnoreCase("PutHDFS_avsc")) {
            properties.put("Directory", nifiOutputBean.get("path") + "/../schema/");
        }
        log.info("new properties: " + properties);
        return properties;
    }


    public String getPath(String type, String database, String tablename, String jobName) {
        String result = null;
        if (type.equalsIgnoreCase("hive")) {
            result = hdfsUtil.getHdfs_path() + database + ".db/" + tablename;
        } else if (type.equalsIgnoreCase("hdfs")) {
            result = hdfsUtil.getHdfs_path() + jobName;
        }
        log.info("Path: " + result);
        return result;
    }


    public boolean updateProcessGroupStatus(String jobId, String action) {
        ProcessGroupDTO byId = niFiRestClientV1.processGroups().findById(jobId, true, true).orElseThrow(() -> new NifiComponentNotFoundException(jobId, NifiConstants.NIFI_COMPONENT_TYPE.PROCESS_GROUP, null));
        FlowSnippetDTO flowSnippetDTO = byId.getContents();
        Set<ProcessorDTO> processors = flowSnippetDTO.getProcessors();
        log.info("update job: " + byId.getName() + ", status: " + action);
        ProcessGroupDTO root = niFiRestClientV1.processGroups().findRoot();
        niFiRestClientV1.processGroups().schedule(byId.getId(), root.getId(), NiFiComponentState.valueOf(action));
        return true;
    }

    public boolean updateProcessGroupScheduler(String jobId, String cron) {
        ProcessGroupDTO byId;
        byId = niFiRestClientV1.processGroups().findById(jobId, true, true).orElseThrow(() -> new NifiComponentNotFoundException(jobId, NifiConstants.NIFI_COMPONENT_TYPE.PROCESS_GROUP, null));
        FlowSnippetDTO flowSnippetDTO = byId.getContents();
        Set<ProcessorDTO> processors = flowSnippetDTO.getProcessors();
        for (ProcessorDTO processorDTO : processors) {
            if (processorDTO.getState().equalsIgnoreCase("RUNNING")) {
                updateProcessGroupStatus(jobId, "STOPPED");
                if (Strings.isNullOrEmpty(cron)) {
                    processorDTO.getConfig().setSchedulingStrategy(NifiFeedConstants.SCHEDULE_STRATEGIES.TIMER_DRIVEN.name());
                    processorDTO.getConfig().setSchedulingPeriod("0 sec");
                } else {
                    processorDTO.getConfig().setSchedulingStrategy(NifiFeedConstants.SCHEDULE_STRATEGIES.CRON_DRIVEN.name());
                    processorDTO.getConfig().setSchedulingPeriod(cron);
                }
                niFiRestClientV1.processors().update(processorDTO);
                updateProcessGroupStatus(jobId, "RUNNING");
            } else {
                if (Strings.isNullOrEmpty(cron)) {
                    processorDTO.getConfig().setSchedulingStrategy(NifiFeedConstants.SCHEDULE_STRATEGIES.TIMER_DRIVEN.name());
                    processorDTO.getConfig().setSchedulingPeriod("0 sec");
                } else {
                    processorDTO.getConfig().setSchedulingStrategy(NifiFeedConstants.SCHEDULE_STRATEGIES.CRON_DRIVEN.name());
                    processorDTO.getConfig().setSchedulingPeriod(cron);
                }
                niFiRestClientV1.processors().update(processorDTO);
            }
        }
        return true;
    }

    public boolean updateProcessGroupInfo(String jobId, String jobInfo, List<String> lables) {
        return true;
    }

    public boolean deleteProcessGroup(String jobId) {
        ProcessGroupDTO byId = niFiRestClientV1.processGroups().findById(jobId, true, true).orElseThrow(() -> new NifiComponentNotFoundException(jobId, NifiConstants.NIFI_COMPONENT_TYPE.PROCESS_GROUP, null));
        niFiRestClientV1.processGroups().delete(byId);
        return true;
    }

    public List<Map<String, Object>> getAllProcessGroups(String query) {
        List<Map<String, Object>> result = Lists.newArrayList();
        ProcessGroupDTO root = niFiRestClientV1.processGroups().findRoot();
        Set<ProcessGroupDTO> all = niFiRestClientV1.processGroups().findAll(root.getId());
        Iterator<ProcessGroupDTO> iterator = all.iterator();
        while (iterator.hasNext()) {
            ProcessGroupDTO next = iterator.next();
            if (query.isEmpty()) {
                Job job = jobDao.findOne(next.getId());
                if (job != null) {
                    try {
                        Map<String, Object> map = MapBeanConvertor.convertToMap(job);
                        String s = "";
                        Optional<ProcessGroupDTO> byId = niFiRestClientV1.processGroups().findById(next.getId(), false, true);
                        FlowSnippetDTO contents = byId.get().getContents();
                        Set<ProcessorDTO> processors = contents.getProcessors();
                        Set<BulletinDTO> bulletinDTOSet = Sets.newHashSet();
                        for (ProcessorDTO processorDTO : processors) {
                            bulletinDTOSet.addAll(niFiRestClientV1.getBulletins(processorDTO.getId()));
                        }
                        for (BulletinDTO bulletinDTO : bulletinDTOSet) {
                            s += bulletinDTO.getMessage() + "\n";
                        }
                        map.put("error", s);
                        result.add(map);
                    } catch (IntrospectionException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Job job = jobDao.findByQuery(next.getId(), query);
                if (job != null) {
                    try {
                        Map<String, Object> map = MapBeanConvertor.convertToMap(job);
                        String s = "";
                        for (BulletinDTO bulletinDTO : niFiRestClientV1.getBulletins(job.getJobId())) {
                            s += bulletinDTO.getMessage() + "\n";
                        }
                        map.put("error", s);
                        result.add(map);
                    } catch (IntrospectionException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }

    public Map<String, Object> getProcessGroupById(String jobId) {
//        ProcessGroupDTO byId = niFiRestClientV1.processGroups().findById(jobId, true, true).orElseThrow(() -> new NifiComponentNotFoundException(jobId, NifiConstants.NIFI_COMPONENT_TYPE.PROCESS_GROUP, null));
        Job job = jobDao.findOne(jobId);
        if (job != null) {
            List<Processor> processorDaoByJobId = processorDao.findByJobId(jobId);
            try {
//                job.setLables(job.getLables());
                Map map = MapBeanConvertor.convertToMap(job);
                map.put("inputConfig", ((Map<String, Object>) JSONObject.parse(processorDaoByJobId.get(0).getConfig())).get("input"));
                return map;
            } catch (IntrospectionException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public List<List<String>> getAllProcessGroupsNexttimes(String currentTime) {
        CronExpParser cronExpParser = new CronExpParser();
//        List<Job> jobList = Lists.newArrayList();
//        ProcessGroupDTO root = niFiRestClientV1.processGroups().findRoot();
//        Set<ProcessGroupDTO> all = niFiRestClientV1.processGroups().findAll(root.getId());
//        Iterator<ProcessGroupDTO> iterator = all.iterator();
//        while (iterator.hasNext()) {
//            ProcessGroupDTO next = iterator.next();
//            Job job = jobDao.findOne(next.getId());
//            if (job != null && job.getState().equalsIgnoreCase("running")) {
//                jobList.add(job);
//            }
//        }

        Iterable<Job> jobList = jobDao.findAll();
        Map<String, String> cronExpLs = Maps.newHashMap();
        for (Job job : jobList) {
            cronExpLs.put(job.getJobName(), job.getCron());
        }
        List<List<String>> parseCronLs4OneDay = cronExpParser.parseCronLs4OneDay(cronExpLs, currentTime);

        return parseCronLs4OneDay;
    }

    public void saveJob(String jobname, String id, String templateid, String jobInfo, List<String> lables, String cron, Map<String, Object> config) {
        Job job = new Job();
        job.setJobName(jobname);
        job.setCreateTime(new Timestamp(System.currentTimeMillis()));
        job.setJobId(id);
        job.setTemplateId(templateid);
        job.setJobInfo(jobInfo);
        job.setLables(Arrays.toString(lables.toArray()));
        job.setCron(cron);
        job.setRunCount(0);
        job.setFailCount(0);
        job.setTotalCount("0");
        if (((Map<String, String>) config.get("output")).get("type").equalsIgnoreCase("hive")) {
            job.setDatabase(((Map<String, String>) config.get("output")).get("database"));
            job.setTable(((Map<String, String>) config.get("output")).get("table"));
        } else if (((Map<String, String>) config.get("output")).get("type").equalsIgnoreCase("hdfs")) {
            job.setTable(((Map<String, String>) config.get("output")).get("path"));
        }
        job.setOutput(((Map<String, String>) config.get("output")).get("type"));
        job.setInput("ftp");
        job.setState("STOPPED");
        log.info("New job: " + job);
        jobDao.save(job);
        log.info("Save job to job table success");
    }

    public void updateJobStatus(String jobId, String action) {
        jobDao.updateJobStatus(jobId, action);
    }

    public void updateJobScheduler(String jobId, String cron) {
        jobDao.updateJobScheduler(jobId, cron);
    }

    public void updateJobInfo(String jobId, String jobInfo, String lables) {
        jobDao.updateJobInfo(jobId, jobInfo, lables);
    }

    public void deleteJob(String jobId) {
        jobDao.delete(jobId);
    }

    public boolean findJobNameIfExist(String jobName) {
        if (jobDao.findJobNameIfExist(jobName) > 0 || hdfsUtil.checkPathExists(jobName)) {
            return true;
        }
        return false;
    }

    public boolean findConnectionIDIfExist(String connectionID) {
        if (processorDao.findConnectionIDIfExist(connectionID) > 0)
            return true;
        return false;
    }

    public List<Map<String, List<Map<String, String>>>> getAllTotalCountByOrder() {
        List<Map<String, List<Map<String, String>>>> result = Lists.newArrayList();
        Map<String, List<Map<String, String>>> hdfs = Maps.newHashMap();
        Map<String, List<Map<String, String>>> hive = Maps.newHashMap();
        List<Map<String, String>> hdfs_list = Lists.newArrayList();
        List<Map<String, String>> hive_list = Lists.newArrayList();

        List<Map<String, String>> allTotalCountByOrder = jobDao.getAllTotalCountByOrder();
        for (Map<String, String> map : allTotalCountByOrder) {
            if (map.get("output").equalsIgnoreCase("hive")) {
                map.remove("output");
                hive_list.add(map);
            } else if (map.get("output").equalsIgnoreCase("hdfs")){
                map.remove("output");
                hdfs_list.add(map);
            }
        }
        hdfs.put("hdfs", hdfs_list);
        hive.put("hive", hive_list);
        result.add(hdfs);
        result.add(hive);
        return result;
    }

    public Map<String, String> getAllTotalCountTogether() {
        List<Map<String, String>> result = jobDao.getAllTotalCountTogether();
        Map<String, String> output = Maps.newHashMap();
        for (Map<String, String> map : result) {
            output.put(map.get("output"), map.get("totalCount"));
        }
        if (!output.containsKey("hive")) {
            output.put("hive", "0");
        }
        if (!output.containsKey("hdfs")) {
            output.put("hdfs", "0");
        }
        return output;
    }


    public String getJobStatus(String jobId) {
        String result = jobDao.getJobStatus(jobId);
        return result;
    }

    public Job findJobIfExist(String jobId) {
        Job result = jobDao.findOne(jobId);
        return result;
    }
}
