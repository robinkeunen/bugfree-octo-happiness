package project.master;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.OperationExecutionException;
import oracle.kv.ValueVersion;
import project.StoreController;
import project.masters.dispatchers.SingleStoreDispatcher;
import project.masters.dispatchers.StoreDispatcher;

public class StoreMaster {
	// Singleton instance
	private static StoreMaster storeMaster;
	private static List<KVStore> kvstores;
	
	private List<StoreController> stores;
	private StoreDispatcher dispatcher;
	
	
	private StoreMaster()  {
		
		this.stores = new ArrayList<StoreController>();
		for (KVStore kvstore: kvstores) {
			this.stores.add(new StoreController(kvstore));
		}
		
		this.dispatcher = new SingleStoreDispatcher();
		this.dispatcher.setStoreNumber(stores.size());
		
	}
	
	public static void setKVStores(List<KVStore> stores) {
		kvstores = stores;
	}
	
	
	public static StoreMaster getStoreMaster() throws MissingConfigurationException {
		if (kvstores == null) {
			throw new MissingConfigurationException("you must set KVStores (setKVStores) before using the MasterStore");
		}
		if (storeMaster == null) {
			storeMaster = new StoreMaster();
		}
		return storeMaster;
	}
	
	public void doProfileTransaction(Long profileKey) throws OperationExecutionException {
		
		// TODO remove this fake timer
		long time = System.currentTimeMillis();
		while (System.currentTimeMillis() - time < 100) {}
		
		StoreController targetStore = stores.get(dispatcher.getStoreIndexForKey(profileKey));
		targetStore.doProfileTransaction(profileKey);
	}

	private void moveProfil(KVStore kv_src, KVStore kv_targ, String profilID) {
		// Read in StoreA
		// TODO manipulate StoreController instead of KVStores
		Key key = Key.createKey(profilID);
		SortedMap<Key, ValueVersion> profilItems = kv_src.multiGet(key, null, null);
		System.out.println("MOVE - GET ITEMS OF "+profilID+" FROM StoreSrc = "+profilItems.size()+" item(s).");
		// Write in StoreB
		for (Map.Entry<Key, ValueVersion> entry : profilItems.entrySet()) {
			System.out.println("MOVE - MOVE "+entry.getKey().getFullPath()+" from StoreSrc to StoreTarg");
			kv_targ.put(entry.getKey(), entry.getValue().getValue());
		}
		// Delete in StoreA
		System.out.println("MOVE - DELETE "+profilID+" from StoreSrc");
		removeProfil(kv_src, profilID);		
	}
	
	private void removeProfil(KVStore kv, String profilID) {
		Key key = Key.createKey(profilID);
		kv.multiDelete(key, null, null);
	}



	

}
