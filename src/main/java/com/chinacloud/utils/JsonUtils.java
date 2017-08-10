package com.chinacloud.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

	public static <T> T jsonToBean(String json, Class<T> cls)
			throws IOException, JsonParseException {
		ObjectMapper mapper = new ObjectMapper();
		return (T) mapper.readValue(json, cls);
	}

	public static <T> T jsonToListBean(String json, TypeReference<T> cls)
			throws IOException, JsonParseException {

		ObjectMapper mapper = new ObjectMapper();
		mapper.readValue(json, cls);
		return mapper.readValue(json, cls);
	}

	public static String beanToJson(Object o) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(o);
		return json;
	}

}
