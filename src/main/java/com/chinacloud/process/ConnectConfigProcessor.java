package com.chinacloud.process;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;
public abstract class ConnectConfigProcessor {	
	public static final Table<String, String, String> connectorTypeNameTable = HashBasedTable.create();
	@SuppressWarnings("unused")
	public  static  Map<String, Object> convertToConfig(String type,final Object bean) 	    { 
	        Map<String, Object> result =Maps.newHashMap(); 
	        Method[] methods = bean.getClass().getDeclaredMethods(); 
	        for (Method method : methods) 
	        { 
	            try 
	            { 
	                if (method.getName().startsWith("get")) 
	                { 
	                    String field = method.getName(); 
	                    field = field.substring(field.indexOf("get") + 3); 
	                    field = field.toLowerCase().charAt(0) + field.substring(1);
	                    Object value = method.invoke(bean, (Object[])null); 
	                    if(value!=null){
//	                       value = null == value ? "" : value.toString();
	                       if(null!=connectorTypeNameTable.get(type, field))
						       result.put(connectorTypeNameTable.get(type, field),value);
	                    }
	                } 
	            } 
	            catch (Exception e) 
	            { 
	            } 
	        } 

	        return result; 
	    } 
	
	public static final void  initTypeNameTable(String fileName) {		
		try {
			Path source = Paths.get(fileName);
			List<String> list=Files.readAllLines(source, StandardCharsets.UTF_8);			
			for (String line : list) {
				if (!Strings.isNullOrEmpty(line)) {
					String[] sarray = line.split("[=]");
					String mapperKey = sarray[0];
					String mapperValue = "";
					if (sarray.length >= 2) {
						mapperValue = sarray[1];
					}
					String[] mapper = mapperKey.split("[@]");
					if (mapper.length==2) {
						String connect = mapper[0];
						String configKey = mapper[1];
						connectorTypeNameTable.put(connect, configKey, mapperValue);						
					} else {
//						logger.warn("数据类型" + line + "无效");
					}
				}
			}
		} catch (IOException e) {
//			logger.debug("load connector file:" + fileName + " IOException", e);
		}		
	}


    public static void main(String[] args) {
		System.err.println(connectorTypeNameTable);
		System.err.println(connectorTypeNameTable.get("jdbcsource", "checkCloumn"));
	}
}
