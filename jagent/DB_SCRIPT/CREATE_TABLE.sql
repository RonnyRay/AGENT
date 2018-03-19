-- Agent数据库脚本

DROP TABLE config;
CREATE TABLE config (name TEXT, value TEXT, desc TEXT, PRIMARY KEY (name));
DROP TABLE task;
CREATE TABLE task (taskId TEXT, planid TEXT, msgType TEXT, taskStatus VARCHAR, extraData , startTime DATETIME, endTime DATETIME, taskData TEXT, PRIMARY KEY (taskId));
