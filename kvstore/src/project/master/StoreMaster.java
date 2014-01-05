package project.master;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.OperationExecutionException;
import oracle.kv.ValueVersion;
import oracle.kv.impl.util.KVThreadFactory;
import project.masters.dispatchers.MultipleStoreDispatcher;
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
	private StoreSupervisor supervisor;
	
	private StoreMaster()  {
		this.stores = new ArrayList<StoreController>();
		int index = 1;
		for (KVStore kvstore: kvstores) { 
			this.stores.add(new StoreController(kvstore, "store " + index));
			index++;
		}	
		this.dispatcher = new MultipleStoreDispatcher(stores.size());
		this.supervisor = new StoreSupervisor();
		new Thread(supervisor).start();
	}
	
	public static void setKVStores(List<KVStore> stores) {
		kvstores = stores;
	}

	/**
	 * @param dispatcher the dispatcher to set
	 */
	public void setDispatcher(StoreDispatcher dispatcher) {
		this.dispatcher = dispatcher;
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
		targetStore.doProfileTransaction(profileKey);
	}

	void moveProfil(StoreController kv_src, StoreController kv_targ, Long profileID) {
		System.out.println("from " + kv_src.getName() + " to " + kv_targ.getName());
		// Notify dispatcher.
		try {
			this.dispatcher.manualMap(profileID, getStoreIndex(kv_targ));
		} catch (UnsupportedException e) {
			
		}
		// Read in Store source.		
		SortedMap<Key, ValueVersion> profilItems = kv_src.getProfile(profileID);
		// Write in Store target.
		try {
			kv_targ.putProfile(profileID, profilItems);
		} catch (OperationExecutionException e) {
			return;
		}
		// Delete in Store source.
		kv_src.removeProfile(profileID);		
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
	

	public List<TransactionMetrics> getTransactionMetrics() {
		List<TransactionMetrics> transactionMetrics = new ArrayList<TransactionMetrics>();
		for (StoreController controller: stores) {
			transactionMetrics.add(controller.getTransactionMetrics());
		}
		return transactionMetrics;
	}

	public void stop() {
		supervisor.setKeepRunning(false);
		for (StoreController controller: stores)
			controller.stop();
	}

	
}
