package com.terran4j.test.commons.reflux;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.terran4j.commons.reflux.server.RefluxServerEndpoint;

@ServerEndpoint("/websocket/connect")
@Component
public class TestServerEndpoint extends RefluxServerEndpoint {
	
	public static final Set<String> clientIds = new HashSet<>();

	public static final String generateClientId() {
		String clientId = UUID.randomUUID().toString();
		clientIds.add(clientId);
		return clientId;
	}
	
	@Override
	protected boolean authenticate(String clientId) {
		return clientIds.contains(clientId);
	}

}
