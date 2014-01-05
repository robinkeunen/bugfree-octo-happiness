package project.master;

import java.util.ArrayList;
import java.util.List;

import project.store.StoreController;
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
			List<StoreController> lstOver = getStoreController(State.OVERLOADED);
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
			}
			//System.out.println("StoreSupervisor - Sleep");
		    try { Thread.sleep(SUPERVISOR_INTERVAL); }
		    catch (InterruptedException e) {}
		}
	}
	
	public List<StoreController> getStoreController(State state) {
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
