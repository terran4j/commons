package com.terran4j.commons.reflux.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.terran4j.commons.reflux.RefluxClient;
import com.terran4j.commons.util.error.BusinessException;
import com.terran4j.commons.util.error.CommonErrorCode;
import com.terran4j.commons.util.task.LoopExecuteTask;

/**
 * 客户端连接管理。
 * 
 * @author wei.jiang
 *
 */
@Service
public class RefluxClientImpl extends LoopExecuteTask implements RefluxClient {

	private static final Logger log = LoggerFactory.getLogger(RefluxClientImpl.class);

	private static final WebSocketContainer container = ContainerProvider.getWebSocketContainer();

	private static final Map<String, ClientConnection> connections = new ConcurrentHashMap<>();

	static final Map<String, ClientConnection> getConnections() {
		return connections;
	}

	@Value("${comm.connect.interval:10000}")
	private long connectInterval;

	@Autowired
	private MessageHandler messageHandler;

	@Autowired
	private ApplicationContext context;

	private Thread connectionRecoverThread = null;

	private volatile boolean inited = false;

	@PostConstruct
	public void init() throws BusinessException {
		if (inited) {
			if (log.isInfoEnabled()) {
				log.info("ClientConnectionService is inited, no need init again.");
			}
			return;
		}

		synchronized (this) {
			if (inited) {
				if (log.isInfoEnabled()) {
					log.info("ClientConnectionService is inited, no need init again.");
				}
				return;
			}
			if (log.isInfoEnabled()) {
				log.info("start to init ClientConnectionService.");
			}

			setSleepTime(connectInterval);

			// 启动连接自动恢复线程。
			connectionRecoverThread = new Thread(this);
			connectionRecoverThread.setDaemon(true);
			connectionRecoverThread.setName("Connection Recover Thread");
			connectionRecoverThread.start();
			if (log.isInfoEnabled()) {
				log.info("自动恢复线程已启动。");
			}

			inited = true;
		}
	}

	@Override
	protected boolean execute() throws Exception {
		reconnectAll();
		return false;
	}

	boolean reconnectAll() {

		// 先过滤掉已经连上的连接。
		List<ClientConnection> targets = new ArrayList<>();
		Collection<ClientConnection> conns = connections.values();
		for (ClientConnection endpoint : conns) {
			if (endpoint != null && !endpoint.isOpen()) {
				targets.add(endpoint);
			}
		}
		if (targets.size() == 0) {
			return false;
		}

		StringBuilder info = new StringBuilder();
		int failedCount = 0;
		info.append("尝试重新连接服务端：\n");
		for (int i = 0; i < targets.size(); i++) {
			ClientConnection conn = targets.get(i);
			String server = conn.getServerURL();
			String clientId = conn.getClientId();
			boolean success = true;
			try {
				success = connect(server, clientId);
			} catch (BusinessException e) {
				log.error("connect server[" + server + "] failed: " + e.getMessage(), e);
				success = false;
			}
			info.append("    连接服务端 ").append(server).append(" ： ");
			info.append(success ? "成功" : "失败").append("\n");
			if (!success) {
				failedCount++;
			}
		}

		if (failedCount == 0) {
			info.append("全部服务端都已连接上，共重新连接上").append(targets.size()).append("个服务端.");
		} else {
			info.append("连接结束，有").append(failedCount).append("个服务端未能连接上，将稍后重试！");
		}
		if (log.isInfoEnabled()) {
			log.info(info.toString());
		}

		return failedCount == 0;
	}

	@Override
	public final boolean connect(String serverURL, String clientId) throws BusinessException {

		try {
			URLEncoder.encode(clientId, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			throw new BusinessException(CommonErrorCode.INVALID_PARAM, e1) //
					.setMessage("参数 clientId 不能进行 URLEncoder 编码： ${clientId}").put("clientId", clientId);
		}
		String url = new StringBuffer(serverURL).append("?clientId=").append(clientId).toString();
		if (log.isInfoEnabled()) {
			log.info("connect server web socket url: {}", url);
		}
		URI endpointURI = null;
		try {
			endpointURI = new URI(url);
		} catch (URISyntaxException e) {
			throw new RuntimeException("连接的URL不正确： " + url, e);
		}

		// 建立 Web Socket 连接。
		ClientConnection oldEndpoint = connections.get(serverURL);
		ClientConnection endpoint = new ClientConnection(serverURL, clientId, messageHandler);
		try {
			Session sesson = container.connectToServer(endpoint, endpointURI);
			if (log.isInfoEnabled()) {
				log.info("目标服务连接成功： {}", url);
			}
			return sesson != null;
		} catch (DeploymentException e) {
			log.error("目标服务部署问题, url = " + url, e);
		} catch (IOException e) {
			log.error("目标服务连接问题, url = " + url, e);
		} finally {
			connections.put(serverURL, endpoint);
		}

		// 如果有旧连接，则释放旧连接。
		if (oldEndpoint != null) {
			try {
				oldEndpoint.close();
			} catch (IOException e) {
				log.error("关闭旧连接失败： " + e.getMessage(), e);
			}
		}

		return false;
	}

	public long getConnectInterval() {
		return connectInterval;
	}

	ApplicationContext getContext() {
		return context;
	}

}
