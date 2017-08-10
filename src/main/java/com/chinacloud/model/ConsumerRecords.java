package com.chinacloud.model;


import java.sql.Timestamp;

/**
 * Created by Administrator on 2017/6/20.
 */
public class ConsumerRecords {
    private Long consumerOffset;

    private Timestamp consumerTime;

    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Long getConsumerOffset() {
        return consumerOffset;
    }

    public void setConsumerOffset(Long consumerOffset) {
        this.consumerOffset = consumerOffset;
    }

    public Timestamp getConsumerTime() {
        return consumerTime;
    }

    public void setConsumerTime(Timestamp consumerTime) {
        this.consumerTime = consumerTime;
    }


}
