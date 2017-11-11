package com.terran4j.commons.reflux.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import com.terran4j.commons.util.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.terran4j.commons.reflux.Message;
import com.terran4j.commons.reflux.RefluxServer;
import com.terran4j.commons.util.Classes;
import com.terran4j.commons.util.Strings;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;

/**
 * 管理所有的 WebSocket 连接。
 * 
 * @author jiangwei
 *
 */
@Service
public class RefluxServerImpl implements RefluxServer {
	
	private static final Logger log = LoggerFactory.getLogger(RefluxServerImpl.class);
	
	private static final Gson gson = new Gson();

	/**
	 * clientId 与 Connection 的关系。
	 */
	private static final Map<String, ClientConnectionInfo> connectionInfoes = new ConcurrentHashMap<>();
	
	@PostConstruct
	public void saveMe() {
		RefluxServerEndpoint.setConnectionManager(this);
	}
	
	public void onClose(String clientId) {
		if (StringUtils.isEmpty(clientId)) {
			return;
		}
		clientId = clientId.trim();
		
		ClientConnectionInfo connInfo = connectionInfoes.remove(clientId);
		if (connInfo == null) {
			return;
		}
		
		if (log.isInfoEnabled()) {
			log.info("关闭一个连接，现在连接数为： {}", getConnectionCount());
		}
	}
	
	public ClientConnectionInfo onOpen(String clientId, RefluxServerEndpoint conn) throws BusinessException {
		if (StringUtils.isEmpty(clientId)) {
			throw new BusinessException(ErrorCodes.NULL_PARAM)
					.put("clientId", clientId);
		}
		
		ClientConnectionInfo connInfo = connectionInfoes.get(clientId);
		if (connInfo == null) { // 首次连接，保留连接信息。
			connInfo = new ClientConnectionInfo();
			connInfo.setClientId(clientId);
			connInfo.setConnectedTime(System.currentTimeMillis());
			connInfo.setConnection(conn);
			connectionInfoes.put(clientId, connInfo);
			if (log.isInfoEnabled()) {
				log.info("有新连接加入! 当前连接数为 {}", getConnectionCount());
			}
		} else { // 重新连接，只更新连接对象就可以了。
			connectionInfoes.get(clientId).setConnection(conn);
			if (log.isInfoEnabled()) {
				log.info("重新建立连接! 当前连接数为 {}", getConnectionCount());
			}
		}
		
		// 开启推送开关。
		conn.setSendable(true);
		
		return connInfo;
	}
	
	public List<RefluxServerEndpoint> getConnections() {
		List<RefluxServerEndpoint> conns = new ArrayList<>();
		
		Iterator<String> it = connectionInfoes.keySet().iterator();
		while (it.hasNext()) {
			String clientId = it.next();
			ClientConnectionInfo info = connectionInfoes.get(clientId);
			if (info == null) {
				continue;
			}
			
			RefluxServerEndpoint conn = info.getConnection();
			if (conn == null) {
				continue;
			}
			
			conns.add(conn);
		}
		return conns;
	}
	
	public RefluxServerEndpoint getConnection(String clientId) {
		ClientConnectionInfo info = connectionInfoes.get(clientId);
		if (info == null) {
			return null;
		}
		return info.getConnection();
	}

	public int getConnectionCount() {
		return connectionInfoes.size();
	}

	public static interface ConnectionHandler {
		void exe(RefluxServerEndpoint conn);
	}

	public void dispatch(ConnectionHandler handler) {
		if (getConnectionCount() <= 0) {
			return;
		}

		Iterator<String> it = connectionInfoes.keySet().iterator();
		while (it.hasNext()) {
			String clientId = it.next();
			RefluxServerEndpoint conn = connectionInfoes.get(clientId).getConnection();
			handler.exe(conn);
		}
	}
	
	@Override
	public final boolean isConnected(String clientId) {
		return connectionInfoes.containsKey(clientId);
	}
	
	@Override
	public final <T> boolean send(T content, String clientId) {
		if (content == null) {
			throw new NullPointerException("message is null.");
		}
		if (clientId == null) {
			throw new NullPointerException("clientId is null.");
		}
		
		if (log.isInfoEnabled()) {
			log.info("start to send message to client【{}】, message:\n{}", // 
					clientId, Strings.toString(content));
		}
		
		ClientConnectionInfo info = connectionInfoes.get(clientId);
		if (info == null) {
			if (log.isWarnEnabled()) {
			    log.warn("Can't get Client Connection by clientId: {}", clientId);
			}			
			return false;
		}
		return sendContent(content, info);
	}
	

	@Override
	public final <T> int sendAll(T content) {
		if (content == null) {
			throw new NullPointerException("message is null.");
		}
		
		if (log.isInfoEnabled()) {
			log.info("start to send message: {}", Strings.toString(content));
		}
		if (connectionInfoes == null || connectionInfoes.size() == 0) {
			if (log.isInfoEnabled()) {
				log.info("NO ANY connection, message won't be sent.");
			}
			return 0;
		}
		
		if (log.isInfoEnabled()) {
			log.info("{} connections from clients", connectionInfoes.size());
		}
		int successCount = 0;
		Collection<ClientConnectionInfo> conns = connectionInfoes.values();
		for (ClientConnectionInfo info : conns) {
			if (sendContent(content, info)){
				successCount++;
			}
		}
		
		return successCount;
	}
	
	final <T> boolean sendContent(T content, ClientConnectionInfo info) {
		if (info == null) {
			return false;
		}
		
		RefluxServerEndpoint conn = info.getConnection();
		if (conn == null) {
			return false;
		}
		
		Message message = new Message();
		message.setId(Message.generateId());
		message.setStatus(Message.STATUS_REQUEST);
		String type = Classes.getTargetClass(content).getName();
		message.setType(type);
		message.setContent(content);
		String messageText = gson.toJson(message);
		if (log.isInfoEnabled()) {
			log.info("send message to client: {}", conn.getClientId());
		}
		
		try {
			if (log.isInfoEnabled()) {
				log.info("sending message: {}", messageText);
			}
			conn.sendMessage(messageText);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			// TODO: 处理异常。
		}
		
		return false;
	}

}
