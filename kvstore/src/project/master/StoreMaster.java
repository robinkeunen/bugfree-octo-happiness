package project.master;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.OperationExecutionException;
import oracle.kv.ValueVersion;
import project.masters.dispatchers.MultipleStoreDispatcher;
import project.masters.dispatchers.SingleStoreDispatcher;
import project.masters.dispatchers.StoreDispatcher;
import project.store.StoreController;

public class StoreMaster {
	// Singleton instance
	private static StoreMaster storeMaster;
	private static List<KVStore> kvstores;
	
	static List<StoreController> stores;
	private StoreDispatcher dispatcher;
	
	private StoreMaster()  {
		
		this.stores = new ArrayList<StoreController>();
		for (KVStore kvstore: kvstores) {
			this.stores.add(new StoreController(kvstore));
		}
		
		this.dispatcher = new MultipleStoreDispatcher(stores.size());
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
		
		StoreController targetStore = stores.get(dispatcher.getStoreIndexForKey(profileKey));
		//System.out.println(dispatcher.getStoreIndexForKey(profileKey));
		targetStore.doProfileTransaction(profileKey);
	}

	static void moveProfil(StoreController kv_src, StoreController kv_targ, String profilID) {
		// Read in StoreA
		// TODO manipulate StoreController instead of KVStores
		Key key = Key.createKey(profilID);
		SortedMap<Key, ValueVersion> profilItems = kv_src.getStore().multiGet(key, null, null);
		System.out.println("MOVE - GET ITEMS OF "+profilID+" FROM StoreSrc = "+profilItems.size()+" item(s).");
		// Write in StoreB
		for (Map.Entry<Key, ValueVersion> entry : profilItems.entrySet()) {
			System.out.println("MOVE - MOVE "+entry.getKey().getFullPath()+" from StoreSrc to StoreTarg");
			kv_targ.getStore().put(entry.getKey(), entry.getValue().getValue());
		}
		// Delete in StoreA
		System.out.println("MOVE - DELETE "+profilID+" from StoreSrc");
		removeProfil(kv_src, profilID);		
	}
	
	static void removeProfil(StoreController kv, String profilID) {
		Key key = Key.createKey(profilID);
		kv.getStore().multiDelete(key, null, null);
	}


	/**
	 * @param dispatcher the dispatcher to set
	 */
	public void setDispatcher(StoreDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}
	
}
