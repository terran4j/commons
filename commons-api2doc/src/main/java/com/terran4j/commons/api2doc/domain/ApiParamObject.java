package com.terran4j.commons.api2doc.domain;

public class ApiParamObject extends ApiObject {
	
	private boolean required;

	private ApiDataType dataType;
	
	private ApiParamLocation location;
	
	private Class<?> sourceType;
	
	public ApiParamObject() {
		super();
		this.setComment("");
	}
	
	public Class<?> getSourceType() {
		return sourceType;
	}

	public void setSourceType(Class<?> sourceType) {
		this.sourceType = sourceType;
	}

	public String getTypeName() {
		if (dataType == null) {
			return "";
		}
		if (dataType.isArrayType()) {
			return dataType.name().toLowerCase() + "[]";
		}
		return dataType.name().toLowerCase();
	}

	public ApiDataType getDataType() {
		return dataType;
	}

	public void setDataType(ApiDataType type) {
		this.dataType = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
	
	public final String getRequiredName() {
		return required ? "是" : "否";
	}

	public ApiParamLocation getLocation() {
		return location;
	}

	public void setLocation(ApiParamLocation location) {
		this.location = location;
	}
	
}
