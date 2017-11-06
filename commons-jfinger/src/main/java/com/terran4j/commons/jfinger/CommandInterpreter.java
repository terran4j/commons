package com.terran4j.commons.jfinger;

import java.util.Properties;

import com.terran4j.commons.util.error.BusinessException;

public interface CommandInterpreter {

	boolean hasOption(String key);

	int getOption(String key, int defaultValue) throws BusinessException;

	String getOption(String key);

	String getOption(String key, String defaultValue);

	Properties getOption(String key, Properties defaultValue);

	void print(String msg);

	void println(String msg);

	void println(String msg, Object... args);

	void println();
}
