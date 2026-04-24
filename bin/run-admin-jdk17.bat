@echo off
setlocal

set ROOT_DIR=%~dp0..
set APP_JAR=%ROOT_DIR%\ruoyi-admin\target\ruoyi-admin.jar

if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=C:\Program Files\Java\jdk-17\bin\java.exe
)

if not exist "%JAVA_EXE%" (
  echo JDK 17 java.exe was not found. Please set JAVA_HOME to a JDK 17 installation.
  exit /b 1
)

if not exist "%APP_JAR%" (
  echo %APP_JAR% was not found. Run from project root:
  echo mvn -pl ruoyi-admin -am -DskipTests package
  exit /b 1
)

"%JAVA_EXE%" -Duser.timezone=Asia/Shanghai -Xms512m -Xmx1024m -jar "%APP_JAR%"
