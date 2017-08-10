package com.chinacloud.bean;

import java.io.Serializable;

/**
 * 非分页返回结果信息
 * @author Administrator
 *
 */
public class ResultInfo implements Serializable {

	private static final long serialVersionUID = -5795089018013798231L;

    private String code;
    
    private String status;

	private String message;
    
    private Object result;
    
    public String getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = Integer.toString(code);
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return message;
	}

	public void setMsg(String msg) {
		this.message = msg;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}


}
