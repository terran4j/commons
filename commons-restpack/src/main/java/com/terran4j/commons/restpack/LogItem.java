package com.terran4j.commons.restpack;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.google.gson.Gson;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;

public class LogItem {

    private static final Gson GSON = new Gson();

    public static final String toJson(LogItem obj) {
        return GSON.toJson(obj);
    }

    public static final LogItem fromJson(String json) {
        return GSON.fromJson(json, LogItem.class);
    }

    public static final LogItem fromEvent(ILoggingEvent event) {
        if (event == null) {
            throw new NullPointerException("ILoggingEvent event is null.");
        }
        LogItem item = new LogItem();

        String loggerName = event.getLoggerName();
        item.setLoggerName(loggerName);

        String message = event.getMessage();
        item.setMessage(message);

        Object[] argObjects = event.getArgumentArray();
        if (argObjects != null) {
            final int length = argObjects.length;
            String[] args = new String[length];
            for (int i = 0; i < length; i++) {
                Object argObject = argObjects[i];
                args[i] = String.valueOf(argObject);
            }
            item.setArgs(args);
        }

        int levelInt = event.getLevel().levelInt;
        item.setLevelInt(levelInt);

        String threadName = event.getThreadName();
        item.setThreadName(threadName);

        long timeStamp = event.getTimeStamp();
        item.setTimeStamp(timeStamp);

        if (event.hasCallerData()) {
            StackTraceElement[] callerData = event.getCallerData();
            if (callerData != null && callerData.length > 0) {
                StackTraceElement caller = callerData[0];
                if (caller != null) {
                    item.setDeclaringClass(caller.getClassName());
                    item.setFileName(caller.getFileName());
                    item.setMethodName(caller.getMethodName());
                    item.setLineNumber(caller.getLineNumber());
                }
            }
        }

        Map<String, String> mdc = MDC.getCopyOfContextMap();
        item.setMdc(mdc);

        return item;
    }

    private String loggerName;

    private String message;

    private String[] args;

    private int levelInt;

    private String threadName;

    private long timeStamp;

    private String serverName;

    private String serverIP;

    private int serverPort;

    private String serviceName;

    private String declaringClass;

    private String methodName;

    private String fileName;

    private int lineNumber;

    private Map<String, String> mdc;

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getArgs() {
        return args;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }

    public int getLevelInt() {
        return levelInt;
    }

    public void setLevelInt(int levelInt) {
        this.levelInt = levelInt;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public Map<String, String> getMdc() {
        return mdc;
    }

    public void setMdc(Map<String, String> mdc) {
        this.mdc = mdc;
    }

    public final String getMdcValue(String key) {
        if (key == null || mdc == null) {
            return null;
        }
        return mdc.get(key);
    }

    public final String getValue(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        if (key.startsWith("x.") && key.length() > 2) {
            key = key.substring(2);
            return getMdcValue(key);
        }

        try {
            String value = BeanUtils.getProperty(this, key);
            return value;
        } catch (IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            return getMdcValue(key);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(args);
        result = prime * result + ((declaringClass == null) ? 0 : declaringClass.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + levelInt;
        result = prime * result + lineNumber;
        result = prime * result + ((loggerName == null) ? 0 : loggerName.hashCode());
        result = prime * result + ((mdc == null) ? 0 : mdc.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
        result = prime * result + ((serverIP == null) ? 0 : serverIP.hashCode());
        result = prime * result + ((serverName == null) ? 0 : serverName.hashCode());
        result = prime * result + serverPort;
        result = prime * result + ((serviceName == null) ? 0 : serviceName.hashCode());
        result = prime * result + ((threadName == null) ? 0 : threadName.hashCode());
        result = prime * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogItem other = (LogItem) obj;
        if (!Arrays.equals(args, other.args))
            return false;
        if (declaringClass == null) {
            if (other.declaringClass != null)
                return false;
        } else if (!declaringClass.equals(other.declaringClass))
            return false;
        if (fileName == null) {
            if (other.fileName != null)
                return false;
        } else if (!fileName.equals(other.fileName))
            return false;
        if (levelInt != other.levelInt)
            return false;
        if (lineNumber != other.lineNumber)
            return false;
        if (loggerName == null) {
            if (other.loggerName != null)
                return false;
        } else if (!loggerName.equals(other.loggerName))
            return false;
        if (mdc == null) {
            if (other.mdc != null)
                return false;
        } else if (!mdc.equals(other.mdc))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (methodName == null) {
            if (other.methodName != null)
                return false;
        } else if (!methodName.equals(other.methodName))
            return false;
        if (serverIP == null) {
            if (other.serverIP != null)
                return false;
        } else if (!serverIP.equals(other.serverIP))
            return false;
        if (serverName == null) {
            if (other.serverName != null)
                return false;
        } else if (!serverName.equals(other.serverName))
            return false;
        if (serverPort != other.serverPort)
            return false;
        if (serviceName == null) {
            if (other.serviceName != null)
                return false;
        } else if (!serviceName.equals(other.serviceName))
            return false;
        if (threadName == null) {
            if (other.threadName != null)
                return false;
        } else if (!threadName.equals(other.threadName))
            return false;
        if (timeStamp != other.timeStamp)
            return false;
        return true;
    }

}
