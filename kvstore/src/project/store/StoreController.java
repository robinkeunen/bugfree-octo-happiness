package project.store;

import java.util.Map;
import java.util.SortedMap;

import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.OperationExecutionException;
import project.Item;
import oracle.kv.ValueVersion;

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
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
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

	/**
	 * @return the monitor
	 */
	public StoreMonitor getMonitor() {
		return monitor;
	}
	
	public SortedMap<Key, ValueVersion> getProfile(Long profileID) {
		Key key = Key.createKey("P"+profileID.toString());
		return this.store.multiGet(key, null, null);		
	}
	
	public void removeProfile(Long profileID) {
		Key key = Key.createKey("P"+profileID.toString());
		this.store.multiDelete(key, null, null);
	}
	 
	public void putProfile(Long profileID, SortedMap<Key, ValueVersion> profilItems) throws OperationExecutionException {
		ProfileTransaction transaction = new ProfileTransaction(store, profileID);
		for (Map.Entry<Key, ValueVersion> entry : profilItems.entrySet()) {
			transaction.addPutOperation(entry.getKey(), entry.getValue().getValue());
		}
		transaction.execute();
		transaction.accept(this.monitor);
	}

	public TransactionMetrics getTransactionMetrics() {
		return monitor.getTransactionMetrics();
	}

	public void stop() {
		monitor.setKeepRunning(false);		
	}
	
}
