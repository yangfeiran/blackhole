package com.chinacloud.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.Primitives;

public class JsonAndBeanConvertor {
	/**
	 * 对象转换成json字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static String beanToJson(Object obj) {
		Gson gson = new Gson();
		return jsonFormatter(gson.toJson(obj));
	}

	/**
	 * 将json转换成object对象
	 * 
	 * @param json
	 * @return
	 */
	public static <T> T jsonToObj(String json, Class<T> classOfT) {
		Object returnObj = new Object();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		returnObj = gson.fromJson(json, classOfT);
		return Primitives.wrap(classOfT).cast(returnObj);
	}
	
	/**
	 * 格式化json字符串
	 * 
	 * @param uglyJSONString
	 * @return
	 */
	public static String jsonFormatter(String uglyJSONString) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJSONString);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}
}