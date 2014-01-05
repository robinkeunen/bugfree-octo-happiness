package project;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import oracle.kv.KVStore;

public class ConfigsServer {
	private final static List<ServerParameters> lstServer = Arrays.asList(
				new ServerParameters("store1", "localhost", "5000"),
				new ServerParameters("store2", "localhost", "5020"),
				new ServerParameters("store3", "localhost", "5030"),
				new ServerParameters("store4", "localhost", "5040"),
				new ServerParameters("store5", "localhost", "5050")
			);
	
	public static List<KVStore> getServersStores() {
		List<KVStore> lstKvstore = new ArrayList<KVStore>();
		for(ServerParameters serv : lstServer) {
			if(serv.getStore()!=null) lstKvstore.add(serv.getStore());
		}
		return lstKvstore;
	}
}
