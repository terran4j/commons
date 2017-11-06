package com.terran4j.commons.jfinger.builtin;

import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.logback.LogbackLoggingSystem;

import com.terran4j.commons.jfinger.Command;
import com.terran4j.commons.jfinger.CommandGroup;
import com.terran4j.commons.jfinger.CommandInterpreter;
import com.terran4j.commons.jfinger.CommandOption;

@CommandGroup(options = {}, desc = "Logback 日志相关命令，可以用于调整日志级别等操作。")
public class LogCommand {

	@Command(desc = "设置日志级别，如： log setLevel -n \"com.terran4j\" -l warn ", options = { //
			@CommandOption(key = "n", name = "loggerName", required = true, desc = "日志名称，一般是用类全名(不支持通配)。"), //
			@CommandOption(key = "l", name = "logLevel", required = true, desc = "日志级别") //
	})
	public void setLevel(CommandInterpreter ci) {
		String loggerName = ci.getOption("n");
		String logLevel = ci.getOption("l");
		logLevel = logLevel.trim().toUpperCase();
		LogLevel level = LogLevel.valueOf(logLevel);
		if (level == null) {
			ci.println("不能识别的logLevel： " + logLevel);
			return;
		}

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		LogbackLoggingSystem logbackLoggingSystem = new LogbackLoggingSystem(classLoader);
		logbackLoggingSystem.setLogLevel(loggerName, level);
		ci.println("成功设置包 {} 的日志级别为： {} ", loggerName, logLevel);
	}
}
