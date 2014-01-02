package project;

import oracle.kv.KVStore;

public class StoreController {
	private KVStore store;

	public StoreController(KVStore kvstore) {
		this.store = kvstore;
	}

	public void doProfileTransaction(Long profileKey) {
		
		for (int i = 0; i < 100; i++) {
			
		}
	}

}
