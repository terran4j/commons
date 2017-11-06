package com.terran4j.commons.api2doc.codewriter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCodeOutput implements CodeOutput {

	private final Map<String, String> codes = new ConcurrentHashMap<>();

	@Override
	public void writeCodeFile(String fileName, String fileContent) {
		codes.put(fileName, fileContent);
	}

	@Override
	public void setPercent(int percent) {
	}

	@Override
	public void log(String log, String... args) {
	}
	
	public String getCode(String fileName) {
		return codes.get(fileName);
	}
	
}
