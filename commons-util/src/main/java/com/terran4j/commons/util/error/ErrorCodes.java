package com.terran4j.commons.util.error;

/**
 * 
 * @author jiangwei
 *
 */
public interface ErrorCodes {

	// 400 
	/**
	 * 用户发出的请求有错误，服务器没有进行新建或修改数据的操作，该操作是幂等的。
	 */
	public static final String INVALID_REQUEST = "invalid.request";

	/**
	 * 无效的请求参数。
	 */
	public static final String INVALID_PARAM = "invalid.param";

	/**
	 * 必填的参数为空。
	 */
	public static final String NULL_PARAM = "null.param";

	/**
	 * 内容非法, 有违禁词等
	 */
	public static final String BLACKLISTED_INPUT = "blacklisted.input";

	// 401
	/**
	 * 表示用户没有权限（令牌、用户名、密码错误）。
	 */
	public static final String AUTH_FAILED = "auth.failed";

	// 403
	/**
	 * 表示用户得到授权（与401错误相对），但是访问是被禁止的。
	 */
	public static final String ACCESS_FORBIDDEN = "access.forbidden";

	public static final String ACCESS_DENY = "access.deny";

	// 404
	/**
	 * 用户发出的请求针对的是不存在的记录，服务器没有进行操作，该操作是幂等的。
	 */
	public static final String RESOURCE_NOT_FOUND = "resource.not.found";

	// 422
	/**
	 * 当创建一个对象时，发生一个验证错误。
	 */
	public static final String UNPROCESABLE_ENTITY = "unprocesable.entity";

	/**
	 * 当创建或更新对象时，对象的关键字段值重复。
	 */
	public static final String DUPLICATE_KEY = "duplicate.key";

	// 500
	/**
	 * 服务器内部错误，用户将无法判断发出的请求是否成功。
	 */
	public static final String INTERNAL_ERROR = "internal.error";

	/**
	 * 服务器未知错误。
	 */
	public static final String UNKNOWN_ERROR = "unknown.error";
	
	/**
	 * 服务器配置错误。
	 */
	public static final String CONFIG_ERROR = "config.error";

}