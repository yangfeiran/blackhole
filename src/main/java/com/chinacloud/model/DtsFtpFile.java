package com.chinacloud.model;
/** * @author  xubo@chinacloud.com.cn
* @date create time：Feb 24, 2017 3:16:25 PM 
* @version 1.0 
 */
public class DtsFtpFile {
	private String name;
	private long size;
	private String lastedUpdateTime;
	private String localPath;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public String getLastedUpdateTime() {
		return lastedUpdateTime;
	}
	public void setLastedUpdateTime(String lastedUpdateTime) {
		this.lastedUpdateTime = lastedUpdateTime;
	}
	public String getLocalPath() {
		return localPath;
	}
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
}
