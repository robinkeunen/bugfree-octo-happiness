package project.store;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import oracle.kv.KVStore;
import oracle.kv.stats.KVStats;
import oracle.kv.stats.OperationMetrics;
import project.Utils;
import project.master.StoreMaster;
import project.store.StoreController.State;

public class StoreMonitor implements Runnable {
	
	private static long SUPERVISOR_INTERVAL = 100;
	private final KVStore store;
	HashMap<Long, Long> itemIds;
	private boolean keepRunning;
	private List<Long> frequentlyAccessed;
	private TransactionMetrics transactionMetrics;
	private String name;
	
	private PrintWriter logger;


	public StoreMonitor(KVStore store, String name) {
		
		this.store = store;
		this.name = name;
		this.transactionMetrics = new TransactionMetrics();
		this.setKeepRunning(true);
		
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
		
		try {
			this.logger = new PrintWriter(name + "_" +
					"f" + TransactionMetrics.FILTER_FACTOR + ".txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		while(isKeepRunning()) {
			//System.out.println("StoreMonitor");
			
			//String prefix = "  " + name + " - " + transactionMetrics.getOperationName() + " - ";
			//System.out.println(prefix + transactionMetrics.getTotalRequests() + " requets");
			//System.out.println(prefix + transactionMetrics.getTotalOps() + " operations");
			//System.out.println(prefix + "min latency " + transactionMetrics.getMinLatencyMs() + " ms");
			//System.out.println(prefix + "avg latency " + transactionMetrics.getAverageLatencyMs() + " ms");
			//System.out.println(prefix + "max latency " + transactionMetrics.getMaxLatencyMs() + " ms");
			
			logger.println(transactionMetrics.getFilteredLatency());
			
		    try { Thread.sleep(SUPERVISOR_INTERVAL); }
		    catch (InterruptedException e) {}
		}
		
		logger.close();
		
	}

	public void updateTransactionMetrics(int latencyMs, int nbOperations) {
		this.transactionMetrics.update(latencyMs, nbOperations);
	}

	public TransactionMetrics getTransactionMetrics() {
		return transactionMetrics;
	}

	private boolean isKeepRunning() {
		return keepRunning;
	}
	
	public void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}
}
