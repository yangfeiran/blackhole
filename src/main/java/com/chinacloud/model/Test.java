package com.chinacloud.model;

import java.util.Date;
import java.sql.*; 
import java.io.*; 

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "test")
public class Test implements Serializable { 

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;
	@Column(name="firstName")
	private String firstName;
	@Column(name="last_name")
	private String last_name;
	@Column(name="age")
	private Integer age;

	public void setId(Integer id){
		this.id = id;
	}
	public Integer getId(){
		return this.id;
	}

	public void setFirstName(String firstname){
		this.firstName = firstname;
	}
	public String getFirstName(){
		return this.firstName;
	}

	public void setLast_name(String last_name){
		this.last_name = last_name;
	}
	public String getLast_name(){
		return this.last_name;
	}

	public void setAge(Integer age){
		this.age = age;
	}
	public Integer getAge(){
		return this.age;
	}
}
