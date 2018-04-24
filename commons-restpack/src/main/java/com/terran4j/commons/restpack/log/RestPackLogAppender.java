package com.terran4j.commons.restpack.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.terran4j.commons.restpack.Log;
import com.terran4j.commons.restpack.LogItem;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RestPackLogAppender<E> extends UnsynchronizedAppenderBase<E> {

    public static final String KEY_LOG_PATTERN = "logPattern";

    public static final String KEY_LOG_ENABLED = "logEnabled";

    private static boolean active = false;

    private static final ThreadLocal<LinkedList<ILoggingEvent>> logs = new ThreadLocal<>();

    private static final ThreadLocal<String> threadLogPattern = new ThreadLocal<>();

    public static boolean isActive() {
        return active;
    }

    public static void logEnabled(String logPattern) {
        // 都用了 ThreadLocal 了，不会有多线程并发的问题，因此不用带并发控制的 Queue 。
        LinkedList<ILoggingEvent> logQueue = new LinkedList<>();
        logs.set(logQueue);
        threadLogPattern.set(logPattern);
    }

    public static void logClear() {
        logs.set(null);
        threadLogPattern.set(null);
    }

//    public static LogItem[] getLogItems() {
//        Queue<ILoggingEvent> queue = logs.get();
//        if (queue == null || queue.size() == 0) {
//            return null;
//        }
//        return queue.toArray(new LogItem[queue.size()]);
//    }

    public static List<String> getLogs() {
        LinkedList<ILoggingEvent> queue = logs.get();
        if (queue == null || queue.size() == 0) {
            return null;
        }

        String logPattern = threadLogPattern.get();
        PatternLayout layout = new PatternLayout();
        if (StringUtils.isEmpty(logPattern)) {
            logPattern = "%date %level -- %-40logger{35}[%line]:%n%msg%n";
        }
        layout.setPattern(logPattern);
        LoggerContext defaultLoggerContext = new LoggerContext();
        layout.setContext(defaultLoggerContext);
        layout.start();

        List<String> messages = new ArrayList<>();
        for (ILoggingEvent event : queue) {
            String msg = layout.doLayout(event);
            messages.add(msg);
        }
        return messages;
    }

    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }

        // 如果程序能执行到这来，说明这个 Appender 是启用的。
        // 这样就可能判断自己是想希望在程序中启用 debug 功能。
        active = true;

        // 只是说明本次请求未启用 RestPackLog 功能。
        Queue<ILoggingEvent> logItemQueue = logs.get();
        if (logItemQueue == null) {
            return;
        }

        if (eventObject instanceof ILoggingEvent) {
            ILoggingEvent logEvent = (ILoggingEvent) eventObject;

            // 本功能产生的日志忽略，以免引起循环调用。
            String loggerName = logEvent.getLoggerName();
            if (loggerName.startsWith(Log.class.getPackage().getName())) {
                return;
            }

            // 记录日志。
//            ILoggingEvent logItem = LogItem.fromEvent(logEvent);
            logItemQueue.add(logEvent);
        }
    }

}