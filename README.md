# XbeeWebProject

REST API implementation which enables to manage remotely **Zigbee Network** based on [Xbee devices](https://www.digi.com/support/productdetail?pid=3430).

Project uses following technologies and libraries:
+ Spring Framework
+ Hibernate
+ HSQLDB
+ Xbee Java Library

## Install
1. Provide **app.properties** file, where you should specify *com.port*(e.g. **COM3**), *baud.rate* (e.g. **9600**) and also *database.url* (e.g. **jdbc:hsqldb:file:C:/path/to/created/hsql/folder**). Example: 
```
com.port=COM3
baud.rate=9600
database.url=jdbc:hsqldb:file:C:/projects/db/hsql
```

2. Specify path to **app.properties** in \apache-tomcat\conf\catalina.properties (just add line in the end -prop.location=**C:/your/path/to/app.properties**)
3. Download **rttxSerial.dll** for you system (if you use 64bit Windows [**download**](http://jlog.org/rxtx-win.html) rttxSerial 64bit file respectively)
4. Create setenv.bat file in **apache-tomcat-9.0.4\bin** directory and add the line in the file - **set CATALINA_OPTS="-Djava.library.path="C:\path\to\your\rttx\""**

Also, in order to provide **log file** you should define [**user environment variable**](https://helpdeskgeek.com/how-to/create-custom-environment-variables-in-windows/) named **XBEE_ROOT** where you specify your project directory or any other.

## Run the app
+ You should create .war file from this project, you can do this either through Tomcat settings using IDEA or using cmd 
> cd /to/your/folder/location 

> mvn package 

and place this .war file in **apache-tomcat-9.0.4\webapps** directory

+ To launch the app you should go to the **apache-tomcat\bin** directory
> cd C:\ ..\apache-tomcat-9.0.4\bin

and start tomcat
> startup

or just launch it in your IDEA

## REST API
To reach your remote web application you should use url type of http://your-server-ip:8080/XbeeWebProject/io/ ... 

where **XbeeWebProject** is just a name of your deployed .war file and **8080** is default tomcat http port [(you can change it)](https://stackoverflow.com/questions/18415578/how-to-change-tomcat-port-number) 
You can manage your ZigBee Network using following queries

### Discovering new devices in network

``` curl -s http://localhost:8080/XbeeWebProject/io/discover?timeout=5000 ```
+ param **timeout** - time of discovering devices in network, ms

### Getting all devices
``` curl -s http://localhost:8080/XbeeWebProject/io/devices ```

### Getting active or non-active devices from db
``` curl -s http://localhost:8080/XbeeWebNew/io/devices?active=true ```

``` curl -s http://localhost:8080/XbeeWebProject/io/devices?active=false ```

+ param **active** - you can specify which devices you want to get (active or not active) by setting it true or false

### Setting sampling rate
``` curl -s -X PUT "http://localhost:8080/XbeeWebProject/io/sampling?adr64bit=0013A20040EC3B01&rate=2000" ```
+ param **rate** - time rate in ms, denotes interval between receiving changed state of pins
+ param **adr64bit** - unique 64 bit address of remote Xbee device

### Setting change detection
``` curl -s -X PUT -d '[0]' -H 'Content-Type: application/json' http://localhost:8080/XbeeWebProject/io/changeDetection?adr64bit=0013A20040EC3B01 ```
+ **Request Body** - set of lines (pins) that are should (or in case if body is empty should not) transmit the state of each specified pin when it is changed. In this example it is **[0]**, that is D0 pin is set up to transmit message when state was changed
+ param **adr64bit** - unique 64 bit address of remote Xbee device

### Writing all changes
``` curl -s -X PUT http://localhost:8080/XbeeWebProject/io/wr?adr64bit=0013A20040EC3B01 ```
+ param **adr64bit** - unique 64 bit address of remote Xbee device

### Getting digital value
Warning - pin should be set up as digital, not analog
``` curl -s GET "http://localhost:8080/XbeeWebProject/io/dio?adr64bit=0013A20040EC3B01&index=0" ```
+ param **adr64bit** - unique 64 bit address of remote Xbee device
+ param **index** - index of a line (pin), in this example it is 0 (D0)

### Setting new identifier (name)
``` curl -s -X PUT "http://localhost:8080/XbeeWebProject/io?adr64bit=0013A20040EC3B01&newId=NewName" ```
+ param **adr64bit** - unique 64 bit address of remote Xbee device
+ param **newId** - new name of remote device which you want to set up

### Getting parameter
Enables to get any AT parameter from remote device. Full list of AT parameters is represented in [device manual](https://www.digi.com/resources/documentation/digidocs/PDFs/90000976.pdf).

``` curl -s GET "http://localhost:8080/XbeeWebProject/io/param?adr64bit=0013A20040EC3B01&at=NI" ```
+ param **adr64bit** - unique 64 bit address of remote Xbee device
+ param **at** - any AT parameter from device manual list. In this example it is NI - node identifier

### Setting parameter
Enables to set up any AT parameter to remote device

``` curl -s -X PUT "http://localhost:8080/XbeeWebProject/io/param?adr64bit=0013A20040EC3B01&at=NI&value=NewIdentifier" ```
+ param **adr64bit** - unique 64 bit address of remote Xbee device
+ param **at** - any AT parameter from device manual list. In this example it is NI - node identifier
+ param **value** - value you want to set up to this parameter

### Getting line states from db
Enables to get all line states which were saved to database

``` curl -s GET "http://localhost:8080/XbeeWebNew/io/states?deviceId=1020" ```

``` curl -s GET "http://localhost:8080/XbeeWebNew/io/states?deviceId=1020&at=D0&startDateTime=2019-12-19T22:15&endDateTime=2019-12-19T22:30" ```


+ param **deviceId** - id of device in database
+ param **at** - AT parameter of line (pin) from which you want to get state, not required
+ param **startDateTime** - start date, format - yyyy-MM-ddTHH:mm:ss, not required
+ param **endDateTime** - end date, format - yyyy-MM-dd'T'HH:mm:ss, not required
