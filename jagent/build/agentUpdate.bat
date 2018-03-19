@echo off 
echo Agent升级器
cd %~dp0 
choice /t 3 /d y /n >nul
@rem 建立数据库备份
if exist .\ALERT_TABLE.sql (
md ..\db-back
copy /y ..\agent.db ..\db-back
)
@rem 复制升级文件
for /f "delims=\" %%a in ('dir /b /a-d /o-d ".\*.*"') do (
  if not %%a == agent.db if not %%a == agentUpdate.bat if not %%a == ALERT_TABLE.sql copy /y .\%%a ..\
) 
cd ..\
start start.bat
exit
