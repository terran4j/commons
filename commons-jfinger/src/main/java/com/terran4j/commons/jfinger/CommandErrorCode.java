package com.terran4j.commons.jfinger;

import java.util.HashSet;
import java.util.Set;

import com.terran4j.commons.util.error.ErrorCode;

public enum CommandErrorCode implements ErrorCode {
	
	// Common Error.
	COMMAND_FORMAT_INVALID(1000510, "command.format.invalid"),
	ARG_QUOTE_NOT_CLOSE(1000511, "arg.quote.not.close"),
	COMMAND_NOT_FOUND_BY_GROUP(1000511, "command.not.found.by.group"),
	COMMAND_NOT_FOUND_BY_NAME(1000512, "command.not.found.by.name"),
	OPTION_KEY_EMPTY(1000513, "command.option.key.is.empty"),
	ARGS_PARSE_ERROR(1000514, "args.parse.error"),
	GET_EXECUTOR_ERROR(1000515, "get.command.executor.error"),
	COMMAND_CLASS_EMPTY(1000516, "command.class.empty"),
	COMMAND_EXECUTE_ERROR(1000517, "command.execute.error"),
	ARG_PARSE_TO_INT_ERROR(1000518, "arg.parse.to.int.error"),
	COMMAND_DEFINE_ERROR(1000519, "command.define.error"),
	UNKNOW_OPTION_TYPE(1000520, "unknow.option.type"),
	COMMAND_SERVICE_NOT_FOUND(1000521, "command.service.not.found"),
	
	// 
	COMMAND_NAME_NOT_FOUND(1000501, "command.name.not.found"),
	COMMAND_NAME_DUPLICATED(1000502, "command.name.duplicated"),
	
	;
	
	private static final Set<Integer> codes = new HashSet<Integer>();
	
	static {
		ErrorCode[] array = values();
		for (ErrorCode code : array) {
			codes.add(code.getValue());
		}
	}
	
	public static final boolean contains(ErrorCode code) {
		if (code == null) {
			return false;
		}
		return codes.contains(code.getValue());
	}

	private final int value;
	
	private final String name;

	private CommandErrorCode(int value, String name) {
		this.value = value;
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public final int getValue() {
		return value;
	}
	
	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

}
