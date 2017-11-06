package com.terran4j.commons.jfinger;

public enum Encoding {

	UTF8("UTF-8"), GBK("GBK");
	
	private String name;
	
	private Encoding(String name){
		this.name = name;
	}
	
	public final String getName() {
		return name;
	}
	
	public static Encoding getDefaultEncoding(){
		return UTF8;
	}
}
