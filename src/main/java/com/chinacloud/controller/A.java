package com.chinacloud.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chinacloud.utils.OkHttp;
import com.google.common.collect.Maps;

public class A {

	public static void main(String[] args) throws IOException {
		/*String url = "http://172.16.80.70:9002/clusters/70/consumers/connect-v%E5%8F%8D%E5%80%92%E6%98%AFvfds/topic/dev_GA_DEMO_KKXX4/type/KF";
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("div.row div.col-md-12 table>tbody>tr");
		long totalLogSize = 0;
		long totalOffset = 0;
		for(int i=0; i<rows.size(); i++){
			Element tr = rows.get(i);
			Elements tds = tr.select("td");
			totalLogSize += Long.parseLong(tds.get(1).text()); 
			if(!tds.get(2).text().equals("-1"))
			    totalOffset += Long.parseLong(tds.get(2).text()); 
		}
		System.out.println(totalOffset);*/
		
		/*String url = "http://172.16.80.70:9002/clusters/70/topics/dev_VIEW_GA_DEMO_KKXX_7";
		Document doc = Jsoup.connect(url).get();
		Elements rows = doc.select("div.row div.col-md-5 table>tbody>tr");
		JSONObject topicInfo = new JSONObject();
		long totalOffset = 0;
		for(int i=0; i<rows.size(); i++){
			Element tr = rows.get(i);
			Elements tds = tr.select("td");
			if(tds.size()==2 && tds.get(0).text().equals("Sum of partition offsets")){
				totalOffset += Long.parseLong(tds.get(1).text()); 
			}
		}
		topicInfo.put("totalSize", totalOffset);
		topicInfo.put("availableSize", totalOffset);*/
		
		Map<String, String> body = Maps.newHashMap();
		body.put("dbName", "");
		body.put("ip", "");
		body.put("port", "");
		body.put("dbType", "MYSQL");
		body.put("additional", "mysql");
		body.put("tableName", "");
		String  data = OkHttp.post("http://10.111.134.92:8099/hubble/metagrid/v2/api/dataSourceManager/owner",
				JSON.toJSONString(body));
		System.out.println(data);
	}

}
