package com.terran4j.commons.reflux.server;

import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.ErrorCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.websocket.*;
import javax.websocket.RemoteEndpoint.Async;
import java.io.EOFException;
import java.io.IOException;
import java.util.List;

/**
 * 客户端应用与服务端应用的连接，可以让服务端向客户端推送消息。<br>
 * 注意： 客户端应用不一定只有一台实例，比如互联网应用一般都有多台同构的实例，每台实例都会连接服务端应用。<br>
 * 同样，服务端应用也不一定只一台实例，每台实例也会被不同的客户端实例所连接。<br>
 * 所以，这里说的“连接通道”，是一个虚拟的概念，指服务端集群与客户端集群建立的连接，屏蔽掉底层实例间连接的细节。<br>
 *
 * @author jiangwei
 */
public abstract class RefluxServerEndpoint implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(RefluxServerEndpoint.class);

    private static RefluxServerImpl connectionManager;

    public static final void setConnectionManager(RefluxServerImpl connectionManager) {
        RefluxServerEndpoint.connectionManager = connectionManager;
    }

    private Session session;

    private ClientConnectionInfo client = null;

    private boolean sendable = false;

    private ApplicationContext context = null;

    public final ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public final void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public final void setSendable(boolean sendable) {
        this.sendable = sendable;
    }

    private void close(Session session) {
        String clientId = getClientId();
        if (connectionManager != null) {
            connectionManager.onClose(clientId);
        }

        if (session != null && session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (this.session != session && this.session != null && session.isOpen()) {
            try {
                this.session.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.session = null;
        this.client = null;
        this.sendable = false;
    }

    protected abstract boolean authenticate(String clientId);

    protected String getClientId(Session session) {
        List<String> clientIds = session.getRequestParameterMap().get("clientId");
        if (clientIds == null || clientIds.size() == 0) {
            log.warn("来自客户端的连接, clientId is empty");
            return null;
        }
        if (clientIds.size() > 1) {
            log.warn("来自客户端的连接, clientId more than one: {}", clientIds);
            return null;
        }
        String clientId = clientIds.get(0);
        if (log.isInfoEnabled()) {
            log.info("来自客户端的连接, clientId = {}", clientId);
        }
        return clientId;
    }

    @OnOpen
    public final void onOpen(Session session) throws BusinessException {
        if (connectionManager == null) {
            throw new BusinessException(ErrorCodes.INTERNAL_ERROR)
                    .setMessage("Server not inited...");
        }

        this.session = session;
        String clientId = getClientId(session);
        if (!authenticate(clientId)) {
            close(session);
            return;
        }
        try {
            client = connectionManager.onOpen(clientId, this);
        } catch (BusinessException e) {
            close(session);
            return;
        }

    }

    @OnClose
    public final void onClose() {
        if (log.isInfoEnabled()) {
            log.info("关闭客户端连接: {}", client);
        }
        close(session);
    }

    @OnMessage
    public final void onMessage(String message, Session session) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("来自收集端的消息: {}", message);
        }
    }

    @OnError
    public final void onError(Session session, Throwable e) {
        if (e instanceof EOFException) {
            System.out.println("连接异常中断.");
            return;
        }
        log.error("Client Connection Error: {}", e.getMessage(), e);
    }

    public final boolean isSendable() {
        return sendable && client != null && session != null && session.isOpen();
    }

    public final void sendMessage(String message) throws IOException {
        if (isSendable()) {
            synchronized (this) {
                Async async = session.getAsyncRemote();
                async.sendText(message);
                if (log.isInfoEnabled()) {
                    log.info("sendMessage done\nclient: {}\nmessage: {}", client, message);
                }
            }
        } else {
            if (log.isInfoEnabled()) {
                log.info("Connection is not sendable, sendable = {}, client = {}, session.isOpen() = {}",
                        sendable, client, session.isOpen());
            }
        }
    }

    ////////////////////////////////////////////////////////////////

    String getClientId() {
        return client == null ? null : client.getClientId();
    }

}
