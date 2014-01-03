package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.kv.KVStore;

public class ConfigsServer {
	private final static List<ServerParameters> lstServer = Arrays.asList(
				new ServerParameters("store1", "127.0.0.1", "5000"),
				new ServerParameters("store2", "127.0.0.1", "5020")
			);
	
	public static List<KVStore> getServersStores() {
		List<KVStore> lstKvstore = new ArrayList<KVStore>();
		for(ServerParameters serv : lstServer) {
			if(serv.getStore()!=null) lstKvstore.add(serv.getStore());
		}
		return lstKvstore;
	}
}
