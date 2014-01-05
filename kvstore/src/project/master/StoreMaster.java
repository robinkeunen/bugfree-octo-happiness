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
import project.masters.dispatchers.UnsupportedException;
import project.store.StoreController;
import project.store.TransactionMetrics;

public class StoreMaster {
	// Singleton instance
	private static StoreMaster storeMaster;
	private static List<KVStore> kvstores;
	
	List<StoreController> stores;
	private StoreDispatcher dispatcher;
	
	private StoreMaster()  {
		
		this.stores = new ArrayList<StoreController>();
		int index = 1;
		for (KVStore kvstore: kvstores) { 
			this.stores.add(new StoreController(kvstore, "store " + index));
			index++;
			
		}
		
		this.dispatcher = new MultipleStoreDispatcher(stores.size());
		
		//new Thread(new StoreSupervisor()).start();
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

	void moveProfil(StoreController kv_src, StoreController kv_targ, Long profilID) {
		// Read in StoreA
		// TODO manipulate StoreController instead of KVStores
		Key key = Key.createKey("P"+profilID.toString());
		SortedMap<Key, ValueVersion> profilItems = kv_src.getStore().multiGet(key, null, null);
		System.out.println("MOVE - GET ITEMS OF P"+profilID+" FROM StoreSrc = "+profilItems.size()+" item(s).");
		// Write in StoreB
		for (Map.Entry<Key, ValueVersion> entry : profilItems.entrySet()) {
			System.out.println("MOVE - MOVE "+entry.getKey().getFullPath()+" from StoreSrc to StoreTarg");
			kv_targ.getStore().put(entry.getKey(), entry.getValue().getValue());
		}
		try {
			this.dispatcher.manualMap(profilID, getStoreIndex(kv_targ));
		} catch (UnsupportedException e) {
			
		}
		// Delete in StoreA
		System.out.println("MOVE - DELETE P"+profilID+" from StoreSrc");
		removeProfil(kv_src, profilID);		
	}
	
	private void removeProfil(StoreController kv, Long profilID) {
		Key key = Key.createKey("P"+profilID.toString());
		kv.getStore().multiDelete(key, null, null);
	}
	
	private int getStoreIndex(StoreController kv) {
		int ind = 0;
		for(StoreController store : this.stores) {
			if(store==kv)
				return ind;
			ind++;
		}
		return -1;
	}

	/**
	 * @param dispatcher the dispatcher to set
	 */
	public void setDispatcher(StoreDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	public List<TransactionMetrics> getTransactionMetrics() {
		List<TransactionMetrics> transactionMetrics = new ArrayList<TransactionMetrics>();
		for (StoreController controller: stores) {
			transactionMetrics.add(controller.getTransactionMetrics());
		}
		return transactionMetrics;
	}
	
}
