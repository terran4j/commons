package com.terran4j.commons.reflux.client;

import java.io.IOException;

import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonSyntaxException;
import com.terran4j.commons.util.error.BusinessException;

/**
 * 客户端的连接。
 * @author wei.jiang
 */
@ClientEndpoint
public class ClientConnection {

	private static final Logger log = LoggerFactory.getLogger(ClientConnection.class);
	
	private final String serverURL;
	
	protected final String clientId;

	private final MessageHandler messageHandler;

	private Session session = null;

	public ClientConnection(String serverURL, String clientId, MessageHandler messageHandler) {
		super();
		this.serverURL = serverURL;
		this.clientId = clientId;
		this.messageHandler = messageHandler;
	}

	public boolean isOpen() {
		return session != null && session.isOpen();
	}

	@OnOpen
	public void onOpen(Session userSession) {
		if (log.isInfoEnabled()) {
			log.info("Opening client websocket, server = {}", serverURL);
		}
		session = userSession;
	}

	@OnClose
	public void onClose(Session userSession, CloseReason reason) {
		if (log.isInfoEnabled()) {
			log.info("Closing client websocket, server = {}, reason: {}", //
					serverURL, reason.toString());
		}
		session = null;
	}

	/**
	 * @param message
	 */
	@OnMessage
	public void onMessage(String message) {
		String reply = null;
		try {
			if (log.isInfoEnabled()) {
				log.info("receive message from {}, message:\n{}", serverURL, message);
			}
			reply = messageHandler.onMessage(message);
		} catch (JsonSyntaxException e) {
			log.error("Can't parse message: \n{}", message, e);
		} catch (BusinessException e) {
			e.printStackTrace();
			// TODO: 异常处理。
		}
		
		try {
			if (reply != null) {
				if (log.isInfoEnabled()) {
					log.info("reply to {}: \n{}", serverURL, reply);
				}
				sendMessage(reply);
			}
		} catch (IOException e) {
			log.error("reply message failed: \n{}", reply, e);
		}
	}

	public void sendMessage(String message) throws IOException {
		if (session != null && session.isOpen()) {
			synchronized (session) {
				if (log.isInfoEnabled()) {
					log.info("sendMessage: \n{}", message);
				}
				session.getAsyncRemote().sendText(message);
			}
		}
	}
	
	public void close() throws IOException {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}

	public final MessageHandler getMessageHandler() {
		return messageHandler;
	}

	public final String getServerURL() {
		return serverURL;
	}

	public final String getClientId() {
		return clientId;
	}
	
}
