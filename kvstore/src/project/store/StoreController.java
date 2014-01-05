package project.store;

import oracle.kv.KVStore;
import oracle.kv.OperationExecutionException;
import project.Item;

public class StoreController {
	public enum State {
	    UNDERLOADED, LOADED, OVERLOADED 
	}
	
	private KVStore store;
	private StoreMonitor monitor;
	
	private State storeState = State.LOADED;
	private String name;

	public StoreController(KVStore kvstore, String name) {
		this.name = name;
		this.store = kvstore;
		this.monitor = new StoreMonitor(store, name);
		Thread worker = new Thread(monitor);
		worker.start();
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
		transaction.accept(this.monitor);
	}
	
	public State getState() {
		return storeState;
	}
	
	public void setState(State state) {
		storeState = state;
	}

	/**
	 * @return the store
	 */
	public KVStore getStore() {
		return store;
	}
}
