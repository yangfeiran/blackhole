/*
Navicat MySQL Data Transfer

Source Server         : 172.16.50.21
Source Server Version : 50632
Source Host           : 172.16.50.21:3306
Source Database       : blackhole

Target Server Type    : MYSQL
Target Server Version : 50632
File Encoding         : 65001

Date: 2017-08-02 09:48:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for connector
-- ----------------------------
DROP TABLE IF EXISTS `connector`;
CREATE TABLE `connector` (
  `id` varchar(40) NOT NULL,
  `name` varchar(128) NOT NULL,
  `modifedTime` datetime NOT NULL,
  `connectorType` varchar(128) NOT NULL COMMENT 'jdbc/http/hdfs/es',
  `status` varchar(128) DEFAULT NULL,
  `owner` varchar(128) DEFAULT NULL,
  `topic` varchar(128) NOT NULL,
  `category` varchar(128) NOT NULL COMMENT 'source、sink',
  `description` varchar(1000) DEFAULT NULL,
  `parentId` varchar(40) NOT NULL,
  `dbName` varchar(255) DEFAULT NULL,
  `tableName` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for connector_history
-- ----------------------------
DROP TABLE IF EXISTS `connector_history`;
CREATE TABLE `connector_history` (
  `id` varchar(40) NOT NULL DEFAULT '',
  `createDate` datetime DEFAULT NULL,
  `receivedCount` int(11) DEFAULT NULL,
  `message` varchar(1000) DEFAULT NULL,
  `status` varchar(16) DEFAULT NULL,
  `sinkId` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for connector_sessions
-- ----------------------------
DROP TABLE IF EXISTS `connector_sessions`;
CREATE TABLE `connector_sessions` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `connectorId` varchar(40) DEFAULT NULL,
  `connectorName` varchar(128) DEFAULT NULL,
  `propname` varchar(256) DEFAULT NULL,
  `propval` varchar(256) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5256 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for dict_dataset
-- ----------------------------
DROP TABLE IF EXISTS `dict_dataset`;
CREATE TABLE `dict_dataset` (
  `id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(200) CHARACTER SET utf8 NOT NULL,
  `alias` varchar(255) CHARACTER SET utf8 DEFAULT NULL,
  `schema` mediumtext CHARACTER SET utf8,
  `previous_schema` mediumtext CHARACTER SET latin1,
  `schema_type` varchar(50) CHARACTER SET latin1 DEFAULT 'JSON' COMMENT 'JSON, Hive, DDL, XML, CSV',
  `properties` text CHARACTER SET utf8,
  `fields` mediumtext CHARACTER SET utf8,
  `urn` varchar(200) COLLATE utf8_bin NOT NULL,
  `source` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT 'The original data source type (for dataset in data warehouse). Oracle, Kafka ...',
  `location_prefix` varchar(200) COLLATE utf8_bin DEFAULT NULL,
  `parent_name` varchar(200) COLLATE utf8_bin DEFAULT NULL COMMENT 'Schema Name for RDBMS, Group Name for Jobs/Projects/Tracking Datasets on HDFS ',
  `storage_type` enum('Table','View','Avro','ORC','RC','Sequence','Flat File','JSON','XML','Thrift','Parquet','Type','Index','Protobuff') CHARACTER SET latin1 DEFAULT NULL,
  `ref_dataset_id` int(11) unsigned DEFAULT NULL COMMENT 'Refer to Master/Main dataset for Views/ExternalTables',
  `status_id` smallint(6) unsigned DEFAULT NULL COMMENT 'Reserve for dataset status',
  `dataset_type` varchar(30) CHARACTER SET latin1 DEFAULT NULL COMMENT 'hdfs, hive, kafka, teradata, mysql, sqlserver, file, nfs, pinot, salesforce, oracle, db2, netezza, cassandra, hbase, qfs, zfs',
  `hive_serdes_class` varchar(300) CHARACTER SET latin1 DEFAULT NULL,
  `is_partitioned` char(1) CHARACTER SET latin1 DEFAULT NULL,
  `partition_layout_pattern_id` smallint(6) DEFAULT NULL,
  `sample_partition_full_path` varchar(256) CHARACTER SET latin1 DEFAULT NULL COMMENT 'sample partition full path of the dataset',
  `source_created_time` int(10) unsigned DEFAULT NULL COMMENT 'source created time of the flow',
  `source_modified_time` int(10) unsigned DEFAULT NULL COMMENT 'latest source modified time of the flow',
  `created_time` int(10) unsigned DEFAULT NULL COMMENT 'wherehows created time',
  `modified_time` int(10) unsigned DEFAULT NULL COMMENT 'latest wherehows modified',
  `wh_etl_job_id` smallint(6) NOT NULL,
  `wh_etl_exec_id` bigint(20) DEFAULT NULL COMMENT 'wherehows etl execution id that modified this record',
  `std_table_id` int(5) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `urn` (`urn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Table structure for job
-- ----------------------------
DROP TABLE IF EXISTS `job`;
CREATE TABLE `job` (
  `job_id` varchar(50) NOT NULL,
  `jobName` varchar(255) NOT NULL,
  `templateId` varchar(50) NOT NULL,
  `cron` varchar(50) NOT NULL,
  `runCount` int(11) NOT NULL,
  `failCount` int(11) NOT NULL,
  `totalCount` varchar(50) NOT NULL,
  `state` varchar(10) NOT NULL,
  `dataExtractTime` datetime DEFAULT NULL,
  `createTime` datetime NOT NULL,
  `lables` varchar(1000) DEFAULT NULL,
  `jobInfo` varchar(4000) DEFAULT NULL,
  `data_base` varchar(255) DEFAULT NULL,
  `tablename` varchar(255) DEFAULT NULL,
  `input` varchar(255) DEFAULT NULL,
  `output` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`job_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for processor
-- ----------------------------
DROP TABLE IF EXISTS `processor`;
CREATE TABLE `processor` (
  `processor_id` varchar(50) NOT NULL,
  `jobId` varchar(50) NOT NULL,
  `config` varchar(6000) NOT NULL,
  PRIMARY KEY (`processor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role` (
  `roleId` varchar(36) NOT NULL,
  `roleName` varchar(20) DEFAULT NULL,
  `roleDesc` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色表';

-- ----------------------------
-- Table structure for role_privilege
-- ----------------------------
DROP TABLE IF EXISTS `role_privilege`;
CREATE TABLE `role_privilege` (
  `roleId` varchar(36) NOT NULL,
  `privilegeName` varchar(36) NOT NULL,
  PRIMARY KEY (`roleId`,`privilegeName`),
  CONSTRAINT `role_privilege_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和权限的关系表';

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role` (
  `userId` varchar(36) NOT NULL,
  `roleId` varchar(36) NOT NULL,
  `tenantId` varchar(36) NOT NULL,
  PRIMARY KEY (`userId`,`roleId`,`tenantId`),
  KEY `FK_user_role_reference` (`roleId`),
  CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`roleId`) REFERENCES `role` (`roleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='用户角色关联表';

-- ----------------------------
-- Procedure structure for pro
-- ----------------------------
DROP PROCEDURE IF EXISTS `pro`;
DELIMITER ;;
CREATE DEFINER=`root`@`%` PROCEDURE `pro`()
BEGIN
declare  i int;
set i =0;
while i < 5000 do
	INSERT INTO za_kkxx VALUES (i, '治安', 'kkxx', '卡口信息', 'flag', '都江堰市成灌高速出口', '58152151sdf', '2016-12-18 20:22:02', 'nefweui_81234bh234', '515684851', '3nu8f-3reu3');
set i = i+1;
end while;
END
;;
DELIMITER ;
