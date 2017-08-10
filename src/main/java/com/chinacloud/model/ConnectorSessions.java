package com.chinacloud.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "connector_sessions")
public class ConnectorSessions implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="connectorid")
	private String connectorId;
	@Column(name="connectorname")
	private String connectorName;
	@Column(name="propname")
	private String propname;
	@Column(name="propval")
	private String propval;

	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return this.id;
	}

	public void setConnectorId(String connectorid){
		this.connectorId = connectorid;
	}
	public String getConnectorId(){
		return this.connectorId;
	}

	public void setConnectorName(String connectorname){
		this.connectorName = connectorname;
	}
	public String getConnectorName(){
		return this.connectorName;
	}

	public void setPropname(String propname){
		this.propname = propname;
	}
	public String getPropname(){
		return this.propname;
	}

	public void setPropval(String propval){
		this.propval = propval;
	}
	public String getPropval(){
		return this.propval;
	}
}
