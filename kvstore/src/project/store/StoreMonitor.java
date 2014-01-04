package project.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import oracle.kv.KVStore;
import oracle.kv.stats.KVStats;
import oracle.kv.stats.OperationMetrics;
import project.master.StoreMaster;
import project.store.StoreController.State;

public class StoreMonitor implements Runnable {
	
	private static long SUPERVISOR_INTERVAL = 1000;
	private final KVStore store;
	HashMap<Long, Long> itemIds;
	private boolean keepRunning;
	private List<Long> frequentlyAccessed;

	public StoreMonitor(KVStore store) {
		this.store = store;
		
		// TODO initiate parameters from store
		this.itemIds = new HashMap<Long, Long>();
		this.frequentlyAccessed = new ArrayList<>(5);
	}
	
	public long getMaxId(long profile) {
		return itemIds.get(profile);
	}
	
	public Long getProfilMaxItems() {
		Long result = null;
		Long max = new Long(-1);
		for(Entry<Long, Long> entry : itemIds.entrySet()) {
		    Long nbItems = entry.getValue();
		    if(nbItems > max) {
		    	Long profilId = entry.getKey();
		    	max = nbItems;
		    	result = profilId;
		    }
		    
		}
		return result;
	}
	
	synchronized public long getAndIncMaxId(long profile) {
		Long max = itemIds.get(profile); 
		if (max == null) {
			max = 0L;
		}
		itemIds.put(profile, max + 1);
		
		return max + 1;
	}

	@Override
	public void run() {
		
		while(keepRunning) {
			System.out.println("StoreMonitor - Start");
			KVStats stats = store.getStats(true);
			List<OperationMetrics> operationMetrics = stats.getOpMetrics();
			System.out.println("StoreMonitor - Sleep");
		    try { Thread.sleep(SUPERVISOR_INTERVAL); }
		    catch (InterruptedException e) {}
		}

	}
}
