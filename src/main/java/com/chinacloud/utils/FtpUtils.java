package com.chinacloud.utils;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.SocketException;
import java.util.List;

/**
 * @author xubo@chinacloud.com.cn
 * @version 1.0
 * @date create time：Feb 24, 2017 3:05:44 PM
 */

@Component("listMapFtp")
public class FtpUtils {
    private static final Logger log = Logger.getLogger(FtpUtils.class);
//    static String host;
//    static String password;
//    static String username;
//    static int port;


    public static void main(String[] args) {

        FTPClient fc = getFTPClient("172.16.50.22", "123", "fc", 21);
//        List<String> list = checkFtpPath(fc, "/home");
//        System.out.println(list);
    }

    /**
     * Get FTPClient Object
     *
     * @param ftpHost     FTP server host
     * @param ftpPassword
     * @param ftpUserName
     * @param ftpPort     FTP port
     * @return
     */
    public static FTPClient getFTPClient(String ftpHost, String ftpPassword,
                                         String ftpUserName, int ftpPort) {

//        if (Strings.isNullOrEmpty(host)) {
//            host = ftpHost;
//            password = ftpPassword;
//            username = ftpUserName;
//            port = ftpPort;
//        } else if (host.equalsIgnoreCase(ftpHost) && password.equalsIgnoreCase(ftpPassword) && username.equalsIgnoreCase(ftpUserName) && port == ftpPort) {
//            return ftpClient;
//        }
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftpHost, ftpPort);
            ftpClient.login(ftpUserName, ftpPassword);
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                log.info("login ftp server failed");
                ftpClient.disconnect();
            } else {
                log.info("login ftp server ok");
            }
        } catch (SocketException e) {
            e.printStackTrace();
            throw new IllegalStateException("FTP Socket Exception ");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("FTP IO Exception ");
        }
        return ftpClient;
    }

    public static List<String> checkFtpPath(FTPClient ftpClient, String path) throws IOException {
        List<String> list = Lists.newArrayList();
        ftpClient.changeWorkingDirectory(path);
        FTPFile[] ftpFiles = ftpClient.listDirectories();
        for (FTPFile ftpFile : ftpFiles) {
            list.add(ftpFile.getName());
        }
        return list;
    }

    /**
     * Close ftp connection
     *
     * @throws IOException function
     */
    public static void closeServer(FTPClient ftpClient) {
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
  /* 
   *//**
     * @param path
     * @return function:get the specify dir file name lists
     * @throws IOException
     *//*
  public List<String> getFileList(String path) throws ParseException { 
    List<String> fileLists = new ArrayList<String>(); 
    // 获得指定目录下所有文件名 
    FTPFile[] ftpFiles = null; 
    try { 
     ftpFiles = ftpClient.listFiles(path); 
    } catch (IOException e) { 
     e.printStackTrace(); 
    } 
    for (int i = 0; ftpFiles != null && i < ftpFiles.length; i++) { 
     FTPFile file = ftpFiles[i]; 
     if (file.isFile()) { 
  System.out.println("文件夹下面的文件====="+file.getName()); 
      fileLists.add(file.getName()); 
     }else if(file.isDirectory()){ 
     System.out.println("文件夹名称为====="+file.getName()); 
     List<String> childLists = getFileList(path + file.getName()+"/"); 
     for(String childFileName : childLists){ 
     fileLists.add(childFileName); 
     String fileType = childFileName.substring(childFileName.lastIndexOf(".")+1,childFileName.length()); 
    System.out.println("文件类型为："+fileType); 
    FtpUtils ftp = new FtpUtils(); 
    if(fileType.equals("txt")){ 
    System.out.println("文件名为："+childFileName); 
    String content = ""; 
    content = ftp.readFile(path + file.getName()+"/"+childFileName); 
  System.out.println("文件内容为："+content); 
    } 
     } 
     } 
    } 
    return fileLists; 
  } 
   */


    /**
     *Get the specify Dir file content
     *
     * @param ftpUserName
     * @param ftpPassword
     * @param ftpPath
     * @param FTPServer
     * @return
     */  
  /*  public String readFileFromFTP(String ftpUserName, String ftpPassword,  
        String ftpPath, String ftpHost, int ftpPort, String fileName) {  
    	StringBuffer resultBuffer = new StringBuffer();  
        FileInputStream inFile = null;  
        InputStream in = null;  
        FTPClient ftpClient = null;  
        log.info("begin reading the path:" + ftpPath + "file...");  
        try {  
            ftpClient = getFTPClient(ftpHost, ftpPassword, ftpUserName,  
                    ftpPort);  
            ftpClient.setControlEncoding("UTF-8");  
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);  
            ftpClient.enterLocalPassiveMode();  
            ftpClient.changeWorkingDirectory(ftpPath);  
            in = ftpClient.retrieveFileStream(fileName);  
        } catch (FileNotFoundException e) {  
            log.error("没有找到" + ftpPath + "文件");  
            e.printStackTrace();  
            return "下载配置文件失败，请联系管理员.";  
        } catch (SocketException e) {  
            logger.error("连接FTP失败.");  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
            logger.error("文件读取错误。");  
            e.printStackTrace();  
            return "配置文件读取失败，请联系管理员.";  
        }  
        if (in != null) {  
            BufferedReader br = new BufferedReader(new InputStreamReader(in));  
            String data = null;  
            try {  
                while ((data = br.readLine()) != null) {  
                    resultBuffer.append(data + "\n");  
                }  
            } catch (IOException e) {  
                logger.error("文件读取错误。");  
                e.printStackTrace();  
                return "配置文件读取失败，请联系管理员.";  
            }finally{  
                try {  
                    ftpClient.disconnect();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }else{  
            logger.error("in为空，不能读取。");  
            return "配置文件读取失败，请联系管理员.";  
        }  
        return resultBuffer.toString();  
    }  */
}
