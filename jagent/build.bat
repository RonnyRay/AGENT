chcp 65001
@echo off
call mvn clean package

copy /y .\agent.db .\target
copy /y .\target\classes\config.properties .\target\agent.properties
copy /y .\build\start.bat .\target
copy /y .\build\agentUpdate.bat .\target
set /p db=是否打包数据库升级脚本(Y/N):
if /i %db% == Y copy /y .\DB_SCRIPT\ALERT_TABLE.sql .\target
echo 打包完成

pause