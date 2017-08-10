package com.chinacloud.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.chinacloud.bean.ResultInfo;
import com.chinacloud.utils.FtpUtils;

/** * @author  xubo@chinacloud.com.cn
* @date create time：Feb 24, 2017 3:02:44 PM 
* @version 1.0 
 */
@RestController
@RequestMapping("/v1/connectors")
public class FtpDirController {
	private static final Logger log = Logger.getLogger(FtpDirController.class);
	@Autowired  
	private FtpUtils ftpUtils;
	
	@RequestMapping(value = "/ftpDir/{url}/{port}", method = RequestMethod.GET)
	@ResponseBody
	public ResultInfo showFtpDir() {
		ResultInfo info = new ResultInfo();
		info.setCode(HttpStatus.OK.value());
		info.setStatus("success");
		return info;
	}
	
}
