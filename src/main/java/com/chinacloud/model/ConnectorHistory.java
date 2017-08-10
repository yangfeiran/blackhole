package com.chinacloud.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "connector_history")
public class ConnectorHistory implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Column(name="createdate")
	private Timestamp createDate;
	@Column(name="receivedcount")
	private Integer receivedCount;
	@Column(name="message")
	private String message;
	@Column(name="status")
	private String status;
	@Column(name="sinkid")
	private String sinkId;

	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}

	public void setCreateDate(Timestamp createdate){
		this.createDate = createdate;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8") 
	public Timestamp getCreateDate(){
		return this.createDate;
	}

	public void setReceivedCount(Integer receivedcount){
		this.receivedCount = receivedcount;
	}
	public Integer getReceivedCount(){
		return this.receivedCount;
	}

	public void setMessage(String message){
		this.message = message;
	}
	public String getMessage(){
		return this.message;
	}

	public void setStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return this.status;
	}

	public void setSinkId(String sinkid){
		this.sinkId = sinkid;
	}
	public String getSinkId(){
		return this.sinkId;
	}
}
