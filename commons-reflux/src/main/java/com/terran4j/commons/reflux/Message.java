package com.terran4j.commons.reflux;

import java.util.UUID;

/**
 * 
 * 
 * @author wei.jiang
 *
 */
public class Message {
	
	public static final int STATUS_REQUEST = -1;
	
	public static final int STATUS_REPLY_SUCCESS = 0;
	
	public static final String generateId() {
		return UUID.randomUUID().toString();
	}

	/**
	 * 消息的id，具有唯一性。
	 */
	private String id;

	/**
	 * 消息的状态：-1 表示请求； 0 表示成功响应； > 1 表示错误码。
	 */
	private int status;

	/**
	 * 消息的类型，在一个服务内唯一。
	 */
	private String type;

	/**
	 * 消息的内容，json串格式。
	 */
	private Object content;

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public final String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public final void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the content
	 */
	public final Object getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public final void setContent(Object content) {
		this.content = content;
	}

	/**
	 * @return the status
	 */
	public final int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public final void setStatus(int status) {
		this.status = status;
	}
	
}
