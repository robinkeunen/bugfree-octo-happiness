package project;

import java.util.ArrayList;
import java.util.List;

import oracle.kv.KVStore;
import project.masters.dispatchers.SingleStoreDispatcher;
import project.masters.dispatchers.StoreDispatcher;

public class StoreMaster {
	
	private List<StoreController> stores;
	private StoreDispatcher dispatcher;
	
	public StoreMaster(List<KVStore> kvstores) {
		
		this.stores = new ArrayList();
		for (KVStore kvstore: kvstores) {
			this.stores.add(new StoreController(kvstore));
		}
		
		this.dispatcher = new SingleStoreDispatcher();
		this.dispatcher.setStoreNumber(1);
		
	}
	
	public void doProfileTransaction(Long profileKey) {
		StoreController targetStore = stores.get(dispatcher.getStoreIndexForKey(profileKey));
		targetStore.doProfileTransaction(profileKey);
	}

}
