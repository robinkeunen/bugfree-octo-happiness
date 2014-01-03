package project;

import java.util.HashMap;

import oracle.kv.KVStore;

public class StoreMonitor {
	
	private final KVStore store;
	HashMap<Long, Long> itemIds;

	public StoreMonitor(KVStore store) {
		this.store = store;
		this.itemIds = new HashMap<Long, Long>();
	}
	
	public long getMaxId(long profile) {
		return itemIds.get(profile);
	}
	
	synchronized public long getAndIncMaxId(long profile) {
		Long max = itemIds.get(profile); 
		if (max == null) {
			max = 0L;
		}
		itemIds.put(profile, max + 1);
		return max + 1;
	}
}
