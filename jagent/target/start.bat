chcp 65001
@echo 设置注册表中cmd的属性
set rr="HKCU\Console\%%SystemRoot%%_system32_cmd.exe"
reg add %rr% /v "FaceName" /t REG_SZ /d Consolas /f>nul
reg add %rr% /v "FontSize" /t REG_DWORD /d 0x00120008 /f>nul
reg add %rr% /v "FontFamily" /t REG_DWORD /d 0x00000036 /f>nul
reg add %rr% /v "FontWeight" /t REG_DWORD /d 0x00000190 /f>nul
java -jar agent.jar
pause