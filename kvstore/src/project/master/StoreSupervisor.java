package project.master;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.kv.Direction;
import oracle.kv.KeyValueVersion;
import oracle.kv.Value;
import project.store.StoreController;
import project.store.StoreController.State;

public class StoreSupervisor implements Runnable {
	
	private boolean keepRunning = true;
	private static int SUPERVISOR_INTERVAL = 1000; 
	
	@Override
	public void run() {
		while(keepRunning) {
			System.out.println("StoreSupervisor - Start");
			
			// DEBUG
			try {
				StoreMaster.getStoreMaster().stores.get(0).setState(State.OVERLOADED);
				StoreMaster.getStoreMaster().stores.get(1).setState(State.UNDERLOADED);
			} catch (MissingConfigurationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// DEBUG
			
			List<StoreController> lstOver = getStoreController(State.OVERLOADED);
			if(lstOver.size() > 0) {
				List<StoreController> lstNotOver = new ArrayList<StoreController>(getStoreController(State.OVERLOADED));
				lstNotOver.addAll(getStoreController(State.LOADED));
				// Check all overloaded store.
				for(StoreController store : lstOver) {
					// If there are no overloaded store.
					if(!lstNotOver.isEmpty()) {
						// Store to move profile.
						StoreController store_targ = lstNotOver.get(0);
						// Get the bigger profile.
						Long profilID = store.getMonitor().getProfilMaxItems();
						if(profilID != null){
							// Move profile.
							System.out.println("StoreSupervisor - Profil to move : P"+profilID);
							try {
								StoreMaster.getStoreMaster().moveProfil(store, store_targ, profilID);
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
			}
			System.out.println("StoreSupervisor - Sleep");
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
	
	public void stopWork() {
		this.keepRunning = false;
	}

}
