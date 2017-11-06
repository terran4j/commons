package com.terran4j.commons.reflux;

/**
 * 连接管理器，用于服务端管理所有的客户端连接通道。<br>
 * 注意：暂时只用于服务端，用于服务端向客户端的逆向推送。<br>
 * TODO: 后续会考虑要不要加上正向推送。<br>
 * 
 * @author wei.jiang
 *
 */
public interface RefluxServer {
	
	/**
	 * 是否有这个客户端的连接。
	 * @param clientId
	 * @return
	 */
	boolean isConnected(String clientId);
	
	/**
	 * 默认的推送方式，特点如下：<br>
	 * 1. 只推送消息，不要返回内容。<br>
	 * 2. 采用尽力而为的送达方式，并且不保证同一个时间点上执行。<br>
	 * 具体来说，就是推送成功的实例，就立即执行客户端的响应函数；<br>
	 * 推送不成功的实例，会重试几次，重试全部都不成功就放弃了。<br>
	 * 3. 如果有多个客户端实例，会全部推送，而不是选其中若干台推送。<br>
	 * 
	 * @param content
	 *            消息内容。
	 */
	<T> int sendAll(T content);
	
	/**
	 * 与 sendAll 类似，唯一不同的是只向一个客户端推送消息（而不是所有客户端）。
	 * 
	 * @param content 消息内容。
	 * @param clientId 客戶端 id 。
	 * @return
	 */
	<T> boolean send(T content, String clientId);

//	/**
//	 * 自定义的推送方式，通过参数设置推送的具体方式。<br>
//	 * TODO: 考虑本方法比较复杂，目前暂不实现。
//	 * @param message
//	 *            消息内容。
//	 * @param replyClass
//	 *            返回内容，如果没有返回内容就设置成<code>java.lang.Void</code>类型。
//	 * @param instanceCount
//	 *            如果为 0 或负数，表示全部实例都要推送，如果是正整数，表示只推送指定数量的实例。
//	 * @param ensureDelivered
//	 *            是否以确保同时送达: <br>
//	 *            如果为true，会保证消息送达到每个实例上，并且每个实例会同时执行响应函数，如果做不到这一点，就都全部不执行。<br>
//	 *            如果为false，会采用尽力而为的送达方式，送达到了就立即执行响应函数，不成功就重试，重试几次都不成功就放弃。<br>
//	 * @return
//	 */
//	<T, V> List<Reply<V>> send(T message, Class<V> replyClass, int instanceCount, boolean ensureDelivered);

}