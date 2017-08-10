package com.chinacloud.bean;

import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class HdfsSinkBean extends ConnectorBean {
	private HdfsConfigBean hdfsConfigBean;
	private String name;
	private String sourceKey = "hdfsSink";
	public String getName() {
		return name;
	}

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		this.sourceKey = sourceKey;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public HdfsConfigBean getHdfsConfigBean() {
		return hdfsConfigBean;
	}

	public void setHdfsConfigBean(HdfsConfigBean hdfsConfigBean) {
		this.hdfsConfigBean = hdfsConfigBean;
	}


	public Map<String, String> getConnectorClass(String connectorType) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(connectorType));
		Map<String, String> rMap = Maps.newHashMap();
		String connectClass = null;
		if (connectorType.equalsIgnoreCase("hive") || connectorType.equalsIgnoreCase("hdfs")) {
			connectClass = "io.confluent.connect.hdfs.HdfsSinkConnector";
		}
		rMap.put("connector.class", connectClass);
		return rMap;
	}

	public Map<String, String> getPartitionerClass(String partitionerType,String partitionField) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(partitionerType));
		Map<String, String> rMap = Maps.newHashMap();
		String partitionerClass = null;
		if (partitionerType.equalsIgnoreCase("Daily")) {
			partitionerClass = "io.confluent.connect.hdfs.partitioner.DailyPartitioner";
			rMap.put("locale","TimeBasedPartitioner");
			rMap.put("timezone","UTC");
		} else if (partitionerType.equalsIgnoreCase("Field")){
			partitionerClass = "io.confluent.connect.hdfs.partitioner.FieldPartitioner";
			rMap.put("partition.field.name",partitionField);
		}
		rMap.put("partitioner.class", partitionerClass);
		return rMap;
	}

	public Map<String, String> getFormatClass(String formatClass) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(formatClass));
		Map<String, String> rMap = Maps.newHashMap();
		String formatType = null;
		if (formatClass.equalsIgnoreCase("AvroFormat")) {
			formatType = "io.confluent.connect.hdfs.avro.AvroFormat";
		} else if (formatClass.equalsIgnoreCase("ParquetFormat")) {
			formatType = "io.confluent.connect.hdfs.parquet.ParquetFormat";
		}
		rMap.put("format.class", formatType);
		return rMap;
	}

}
