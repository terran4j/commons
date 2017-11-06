package com.terran4j.commons.util;

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Server {

	private static final Logger log = LoggerFactory.getLogger(Server.class);
	
	private static volatile Server v = null;
	
	private static final Server getInstance() {
		if (v != null) {
			return v;
		}
		synchronized (Server.class) {
			if (v != null) {
				return v;
			}
			v = new Server();
			return v;
		}
	}

	private String serverIP;

	private String serverName;

	/**
	 * 注意： InetAddress.getLocalHost() 是耗时操作，因此使用懒加载方式。
	 */
	private Server() {
		super();
		
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (Exception e) {
			log.error("GetLocalHost failed: " + e.getMessage(), e);
		}

		if (address == null) {
			this.serverName = null;
			this.serverIP = null;
		} else {
			this.serverName = address.getHostName();
			this.serverIP = address.getHostAddress();
		}
		if (log.isInfoEnabled()) {
			log.info("本机名称是：{}, IP是： {}", serverName, serverIP);
		}
	}

	public static final String getServerIP() {
		return getInstance().serverIP;
	}

	public static final String getServerName() {
		return getInstance().serverName;
	}

}