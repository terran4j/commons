package com.terran4j.commons.util.error;

public enum CommonErrorCode implements ErrorCode {

	/**
	 * 请求参数非法
	 */
	UNKNOWN_ERROR(1, ErrorCodes.UNKNOWN_ERROR),
	
	/**
	 * 请求参数非法
	 */
	INVALID_PARAM(2, ErrorCodes.INVALID_PARAM, "key, value"),
	
	/**
	 * 请求参数为空。
	 */
	NULL_PARAM(3, ErrorCodes.NULL_PARAM, "key"),
	
	/**
	 * 配置错误。
	 */
	CONFIG_ERROR(4, ErrorCodes.CONFIG_ERROR),
	
	/**
	 * 解析XML出错。
	 */
	XML_ERROR(5, "xml.error"),
	
	/**
	 * 资源找不到。
	 */
	RESOURCE_NOT_FOUND(6, ErrorCodes.RESOURCE_NOT_FOUND, "type, keyName, keyValue"),
	
	/**
	 * 解析 json 串出错。
	 */
	JSON_ERROR(7, "json.error", "jsonText"),
	
	/**
	 * 参数太长了
	 */
	PARAM_LENGTH_TOO_LONG(8, "param.length.too.long", "key, maxLength, acutalLength"),
	
	/**
	 * 关键字段重复。
	 */
	DUPLICATE_KEY(9, ErrorCodes.DUPLICATE_KEY, "key, value"),
	
	/**
	 * 内部错误。
	 */
	INTERNAL_ERROR(10, ErrorCodes.INTERNAL_ERROR),
	;
	
	public static final String KEY = "key";
	
	public static final String VALUE = "value";
	
	public static final String MAX_LENGTH = "maxLength";
	
	public static final String ACUTAL_LENGTH = "acutalLength";
	
	private final int value;
	
	private final String name;
	
	private final String[] requiredFields;

	private CommonErrorCode(int value, String name) {
		this.value = value;
		this.name = name;
		this.requiredFields = new String[]{""};
	}
	
	private CommonErrorCode(int value, String name, String[] requiredFields) {
		this.value = value;
		this.name = name;
		this.requiredFields = requiredFields;
	}
	
	private CommonErrorCode(int value, String name, String requiredFields) {
		this.value = value;
		this.name = name;
		this.requiredFields = ErrorCode.toArray(requiredFields);
	}

	public final int getValue() {
		return value;
	}
	
	public final String getName() {
		return name;
	}

	public final String[] getRequiredFields() {
		return requiredFields;
	}
	
}
