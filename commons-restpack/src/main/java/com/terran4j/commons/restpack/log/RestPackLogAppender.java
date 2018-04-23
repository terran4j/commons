package com.terran4j.commons.restpack.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class RestPackLogAppender<E> extends UnsynchronizedAppenderBase<E> {
	
	@Override
	protected void append(E eventObject) {
		if (!isStarted()) {
			return;
		}
		
		if (eventObject instanceof ILoggingEvent) {
			ILoggingEvent logEvent = (ILoggingEvent) eventObject;
			
			// dlog 程序本身的日志不应该计入。
			String loggerName = logEvent.getLoggerName();
		}
	}
	
}
