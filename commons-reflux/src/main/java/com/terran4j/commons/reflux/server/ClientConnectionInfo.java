package com.terran4j.commons.reflux.server;

public final class ClientConnectionInfo {

	private String envName;

	private String clientId;

	private long connectedTime;

	private RefluxServerEndpoint connection;

	/**
	 * @return the envName
	 */
	public final String getEnvName() {
		return envName;
	}

	/**
	 * @param envName
	 *            the envName to set
	 */
	public final void setEnvName(String envName) {
		this.envName = envName;
	}

	/**
	 * @return the clientId
	 */
	public final String getClientId() {
		return clientId;
	}

	/**
	 * @param clientId
	 *            the clientId to set
	 */
	public final void setClientId(String clientId) {
		this.clientId = clientId;
	}

	/**
	 * @return the connectedTime
	 */
	public final long getConnectedTime() {
		return connectedTime;
	}

	/**
	 * @param connectedTime
	 *            the connectedTime to set
	 */
	public final void setConnectedTime(long connectedTime) {
		this.connectedTime = connectedTime;
	}

	/**
	 * @return the connection
	 */
	public final RefluxServerEndpoint getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public final void setConnection(RefluxServerEndpoint connection) {
		this.connection = connection;
	}

	@Override
	public String toString() {
		return "ClientConnectionInfo [envName=" + envName + ", clientId=" + clientId + ", connectedTime=" + connectedTime
				+ "]";
	}

}