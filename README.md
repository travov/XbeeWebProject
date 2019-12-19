# XbeeWebProject

REST API implementation which enables to manage remotely Zigbee Network based on Xbee devices.

Project uses following technologies and libraries:
+ Spring Framework
+ Hibernate
+ HSQLDB
+ Xbee Java Library

## How to use:
Before you launch the app you must:
1. Provide **app.properties** file, where you should specify *com.port*(e.g. **COM3**), *baud.rate* (e.g. **9600**) and also *database.url* (e.g. **jdbc:hsqldb:file:C:/path/to/created/hsql/folder**)
2. Specify path to **app.properties** in \apache-tomcat\conf\catalina.properties (just add line in the end -prop.location=**C:/your/path/to/app.properties**)
3. Download **rttxSerial.dll** for you system (if you use 64bit Windows download rttxSerial 64bit file respectively) - http://jlog.org/rxtx-win.html
4. Create setenv.bat file in **apache-tomcat-9.0.4\bin** directory and add the line in the file - **set CATALINA_OPTS="-Djava.library.path="C:\path\to\your\rttx\""**

Then you should create .war file from this project, you can do this either through Tomcat settings using IDEA or using cmd 
> cd /to/your/folder/location 

> jar -cvf my_web_app.war * 

and place this .war file in **apache-tomcat-9.0.4\webapps** directory

To launch the app you should go to the **apache-tomcat\bin** directory
> cd C:\ ..\apache-tomcat-9.0.4\bin

and start tomcat
> startup

or just launch it in your IDEA

## REST API
To reach your remote web application you should use url type of http://your-server-ip:8080/XbeeWebProject/io/ ... 

where **XbeeWebProject** is just name of your deployed .war file
You can manage your ZigBee Network using following queries

+ Discovering new devices in network
> /discover?timeout=
>> param timeout
