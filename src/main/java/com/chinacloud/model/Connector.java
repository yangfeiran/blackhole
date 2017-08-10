package com.chinacloud.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "connector")
public class Connector implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	private String id;
	@Column(name="name")
	private String name;
	@Column(name="modifedtime")
	private Timestamp modifedTime;
	@Column(name="connectortype")
	private String connectorType;
	@Column(name="status")
	private String status;
	@Column(name="owner")
	private String owner;
	@Column(name="topic")
	private String topic;
	@Column(name="category")
	private String category;
	@Column(name="description")
	private String description;
	@Column(name="parentid")
	private String parentId;
	@Column(name="dbname")
	private String dbName;
	@Column(name="tablename")
	private String tableName;

	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}

	public void setModifedTime(Timestamp modifedtime){
		this.modifedTime = modifedtime;
	}
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8") 
	public Timestamp getModifedTime(){
		return this.modifedTime;
	}

	public void setConnectorType(String connectortype){
		this.connectorType = connectortype;
	}
	public String getConnectorType(){
		return this.connectorType;
	}

	public void setStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return this.status;
	}

	public void setOwner(String owner){
		this.owner = owner;
	}
	public String getOwner(){
		return this.owner;
	}

	public void setTopic(String topic){
		this.topic = topic;
	}
	public String getTopic(){
		return this.topic;
	}

	public void setCategory(String category){
		this.category = category;
	}
	public String getCategory(){
		return this.category;
	}

	public void setDescription(String description){
		this.description = description;
	}
	public String getDescription(){
		return this.description;
	}

	public void setParentId(String parentid){
		this.parentId = parentid;
	}
	public String getParentId(){
		return this.parentId;
	}
}
