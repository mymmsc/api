# Set root category priority to INFO and its only appender to CONSOLE.
log4j.rootCategory=INFO, LOGFILE, CONSOLE

# LOGFILE is set to be a File appender using a PatternLayout.
log4j.appender.LOGFILE=org.apache.log4j.DailyRollingFileAppender
log4j.appender.LOGFILE.File=${logs.root}/apps/${project}/runtime.log
log4j.appender.LOGFILE.Append=true
log4j.appender.LOGFILE.DatePattern='.'yyyyMMdd
log4j.appender.LOGFILE.layout=org.apache.log4j.PatternLayout
log4j.appender.LOGFILE.layout.ConversionPattern=%d [%t] %c{1} %p - %m%n

# CONSOLE is set to be a console appender using a PatternLayout.
log4j.appender.CONSOLE=org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout=org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=%d [%t] %c{1} %p - %m%n

#log4j.appender.SOCKET=org.apache.log4j.net.SocketAppender
#log4j.appender.SOCKET.RemoteHost=localhost
#log4j.appender.SOCKET.Port=16720

#log4j.appender.TELNET=org.apache.log4j.net.TelnetAppender
#log4j.appender.TELNET.Port=16723
#log4j.appender.TELNET.layout=org.apache.log4j.PatternLayout
#log4j.appender.TELNET.layout.ConversionPattern=%d [%t] %c{1} %p - %m%n
