package com.chinacloud.utils;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * Created by Administrator on 2017/7/25.
 */
@Component("hdfsUtil")
@ConfigurationProperties(prefix = "datafeed_hdfs")
public class HDFSUtil {
    private String hdfs_url;
    private String hdfs_username;
    private String hdfs_path;

    public String getHdfs_path() {
        return hdfs_path;
    }

    public void setHdfs_path(String hdfs_path) {
        this.hdfs_path = hdfs_path;
    }

    public String getHdfs_url() {
        return hdfs_url;
    }

    public void setHdfs_url(String hdfs_url) {
        this.hdfs_url = hdfs_url;
    }

    public String getHdfs_username() {
        return hdfs_username;
    }

    public void setHdfs_username(String hdfs_username) {
        this.hdfs_username = hdfs_username;
    }


    public boolean checkPathExists(String jobName) {
        Configuration configuration = new Configuration();
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(URI.create(hdfs_url), configuration, hdfs_username);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            boolean result= fileSystem.isDirectory(getAbsolutePath(jobName));
            fileSystem.close();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Path getAbsolutePath(String jobName) {
            return new Path(hdfs_path+jobName);
    }

    public int countFilesInPath(String path){
        Configuration configuration = new Configuration();
        FileSystem fileSystem = null;
        try {
            fileSystem = FileSystem.get(URI.create(hdfs_url), configuration, hdfs_username);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            FileStatus[] fileStatuses = fileSystem.listStatus(getAbsolutePath(path));
            fileSystem.close();
            return fileStatuses.length;
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
        }
        return 0;
    }

    public static void main(String[] args) {
        System.setProperty("hadoop.home.dir", "E:\\Downloads\\hadoop-common-2.2.0-bin-master");
        HDFSUtil hdfsUtil = new HDFSUtil();
        boolean b = hdfsUtil.checkPathExists("/opt/server");
        System.out.println("===========================" + b);
    }
}
