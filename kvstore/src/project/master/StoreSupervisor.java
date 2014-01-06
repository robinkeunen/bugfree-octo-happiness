package project.master;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import project.store.StoreController;
import project.store.TransactionMetrics;
import project.store.StoreController.State;

public class StoreSupervisor implements Runnable {
	
	private boolean keepRunning = true;
	private static int SUPERVISOR_INTERVAL = 1000; 
	
	public void stop() {
		this.setKeepRunning(false);
		try {
			for(StoreController storeCntr : StoreMaster.getStoreMaster().stores) {
				storeCntr.getMonitor().setKeepRunning(false);
			}
		} catch (MissingConfigurationException e) { }		
	}
	
	@Override
	public void run() {
		while(isKeepRunning()) {
			System.out.println("StoreSupervisor - Start");
			try {
				List<TransactionMetrics> metrics = StoreMaster.getStoreMaster().getTransactionMetrics();
				float cumul = 0;
				double[] values = new double[metrics.size()];
				int nbVal = 0;
				for(TransactionMetrics metric : metrics) {
					values[nbVal] = metric.getFilteredLatency();
					cumul += values[nbVal++];
				}
				float average = cumul/(float)nbVal;
				cumul = 0;
				for(int i = 0; i < values.length; i++) {
					cumul += java.lang.Math.pow((values[i] - average), (float)2.0);
				}				
				double mean = java.lang.Math.sqrt(cumul/(float)nbVal);
				StandardDeviation stdDev = new StandardDeviation();
				double deviation = stdDev.evaluate(values, mean);
				System.out.println("MEAN = "+mean+" | STD_DEV = "+deviation);
				for(StoreController storeCntr : StoreMaster.getStoreMaster().stores) {
					float latency = storeCntr.getMonitor().getTransactionMetrics().getFilteredLatency();
					if(latency > mean + deviation && latency > 100)
						storeCntr.setState(State.OVERLOADED);
					else if(latency < mean - deviation)
						storeCntr.setState(State.UNDERLOADED);
					else
						storeCntr.setState(State.LOADED);
				}
				
			} catch (MissingConfigurationException e1) {
				// TODO Auto-generated catch block
			}
			
			StoreController mostOverloaded = getMostOverloaded();
			if(mostOverloaded != null) {
				StoreController mostUnderloaded = getMostUnderloaded();
				if(mostUnderloaded!=null) {
					// Get the bigger profile. 
					Long profilID = mostOverloaded.getMonitor().getProfilMaxItems();
					if(profilID != null){
						// Move profile.
						try {
							System.out.println("StoreSupervisor - Profil to move : P"+profilID);
							StoreMaster.getStoreMaster().moveProfil(mostOverloaded, mostUnderloaded, profilID);
							System.out.println("StoreSupervisor - Profil moved");
							// TODO : Update frequentlyAccessed.
						} catch (MissingConfigurationException e) {

						}	
					}
					else
						System.out.println("StoreSupervisor - No Profil selected");					
				}
			}
			/*List<StoreController> lstOver = getStoreController(State.OVERLOADED);
			// Check all overloaded store.
			for(StoreController store : lstOver) {
				List<StoreController> lstNotOver = new ArrayList<StoreController>(getStoreController(State.UNDERLOADED));
				lstNotOver.addAll(getStoreController(State.LOADED));
				// If there are no overloaded store.
				if(!lstNotOver.isEmpty()) {
					// Store to move profile.
					StoreController store_targ = lstNotOver.get(0);
					// Get the bigger profile. 
					Long profilID = store.getMonitor().getProfilMaxItems();
					if(profilID != null){
						// Move profile.
						try {
							System.out.println("StoreSupervisor - Profil to move : P"+profilID);
							StoreMaster.getStoreMaster().moveProfil(store, store_targ, profilID);
							System.out.println("StoreSupervisor - Profil moved");
							// TODO : Update frequentlyAccessed.
						} catch (MissingConfigurationException e) {

						}	
					}
					else
						System.out.println("StoreSupervisor - No Profil selected");
				}
				else {
					System.out.println("StoreSupervisor - A Store is overloaded but no store available to move profiles.");
					break;
				}				
			} */
			System.out.println("StoreSupervisor - Sleep");
		    try { Thread.sleep(SUPERVISOR_INTERVAL); }
		    catch (InterruptedException e) {}
		}
	}
	
	private StoreController getMostOverloaded() {
		List<StoreController> stores = getStoreController(State.OVERLOADED);
		StoreController ret = null;
		float max = -1;
		for(StoreController store : stores) {
			float latency = store.getTransactionMetrics().getFilteredLatency();
			if(latency>max) {
				max = latency;
				ret = store;
			}
		}
		return ret;
	}
	
	private StoreController getMostUnderloaded() {
		List<StoreController> lstNotOver = new ArrayList<StoreController>(getStoreController(State.UNDERLOADED));
		lstNotOver.addAll(getStoreController(State.LOADED));
		StoreController ret = null;
		if(!lstNotOver.isEmpty()) {
			float min = lstNotOver.get(0).getTransactionMetrics().getFilteredLatency();
			for(StoreController store : lstNotOver) {
				float latency = store.getTransactionMetrics().getFilteredLatency();
				if(latency<min) {
					min = latency;
					ret = store;
				}
			}
		}
		return ret;
	}
	
	private List<StoreController> getStoreController(State state) {
		List<StoreController> ret = new ArrayList<StoreController>();
		try {
			for(StoreController storeCntr : StoreMaster.getStoreMaster().stores) {
				if(storeCntr.getState() == state)
					ret.add(storeCntr);
			}
		} catch (MissingConfigurationException e) { }		
		return ret;
	}

	public boolean isKeepRunning() {
		return keepRunning;
	}

	public void setKeepRunning(boolean keepRunning) {
		this.keepRunning = keepRunning;
	}
}
