package project;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;

public class ServerParameters {

	private String storeName = "kvstore";
	private String hostName = "localhost";
	private String hostPort = "5000";
	private final KVStore store;
	
	/**
	 * @param storeName
	 * @param hostName
	 * @param hostPort
	 */
	public ServerParameters(String storeName, String hostName, String hostPort) {
		this.storeName = storeName;
		this.hostName = hostName;
		this.hostPort = hostPort;
		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}
	
	/**
	 * @return the store
	 */
	public KVStore getStore() {
		return store;
	}

	/**
	 * @return the storeName
	 */
	public String getStoreName() {
		return storeName;
	}
	
	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * @return the hostPort
	 */
	public String getHostPort() {
		return hostPort;
	}	
	
}
