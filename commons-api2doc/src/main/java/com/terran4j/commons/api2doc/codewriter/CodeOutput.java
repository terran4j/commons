package com.terran4j.commons.api2doc.codewriter;

public interface CodeOutput {

	void writeCodeFile(String fileName, String fileContent);
	
	void setPercent(int percent);
	
	void log(String log, String... args);
}
