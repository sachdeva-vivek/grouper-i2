REM $Header: /home/hagleyj/i2mi/signet/util/dist-signet_cp.bat,v 1.11 2008-05-18 23:05:22 ddonn Exp $
REM
REM This file is intended to be called from each of the run.bat files in
REM the subdirectories of util. DOS Batch does not provide a dynamic means of
REM generating a classpath as done in each run.sh. 
REM
REM You may need to change SIGNET_LIBS to point to YOUR installation!
REM
IF NOT "%1"=="" GOTO :SETPARAM
set SIGNET_LIBS=..\lib
GOTO :DOSETS
:SETPARAM
set SIGNET_LIBS=%1
:DOSETS
REM echo Setting SIGNET_LIBS to %SIGNET_LIBS%
set CLASSPATH=%CLASSPATH%;..\config
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\activation.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\antlr-2.7.6.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\asm-attrs.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\asm.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\c3p0-0.9.0.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\cglib-2.1.3.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-beanutils-1.7.0.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-collections-3.2.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-dbcp-1.2.1.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-digester-1.7.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-lang-2.0.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-logging-1.1.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\commons-pool-1.3.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\dom4j-1.6.1.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\ehcache-1.2.3.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\hibernate3.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\hsqldb.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jaxb-api.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jaxb-impl.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jaxb-xjc.jar
REM set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jconn2.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jdbc2_0-stdext.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jdom.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jsr173_1.0_api.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jta.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\jTDS2.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\junit-4.1.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\log4j-1.2.11.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\postgresql-8.1-404.jdbc2.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\postgresql-8.1-404.jdbc2ee.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\postgresql-8.1-404.jdbc3.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\servlet-api.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\@CFG_UTIL_API_TOKEN@
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\@CFG_UTIL_UI_TOKEN@
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\@CFG_UTIL_UTIL_TOKEN@
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\@CFG_UTIL_XA_TOKEN@
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\@CFG_UTIL_XB_TOKEN@
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\stax-api-1.0.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\struts.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\subject-0.3.0-rc1-cvs.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\wstx.jar
set CLASSPATH=%CLASSPATH%;%SIGNET_LIBS%\xml-apis.jar