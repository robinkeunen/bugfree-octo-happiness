package project.store;

import oracle.kv.KVStore;
import oracle.kv.OperationExecutionException;
import project.Item;

public class StoreController {
	private KVStore store;
	private StoreMonitor monitor;

	public StoreController(KVStore kvstore) {
		this.store = kvstore;
		this.monitor = new StoreMonitor(store);
	}

	public void doProfileTransaction(Long profileKey) throws OperationExecutionException {
		Item item = null;
		ProfileTransaction transaction = new ProfileTransaction(store, profileKey);
		for (int i = 0; i < 100; i++) {

			long itemId = monitor.getAndIncMaxId(profileKey);
			item = Item.createRandomItem();
			item.setItemId(itemId);
			
			transaction.addPutOperation(item);
			
		}
		transaction.execute();
	}

}
