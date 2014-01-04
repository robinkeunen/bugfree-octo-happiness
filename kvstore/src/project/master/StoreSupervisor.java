package project.master;

import java.util.ArrayList;
import java.util.List;

import project.store.StoreController;
import project.store.StoreController.State;

public class StoreSupervisor implements Runnable {
	
	private boolean keepRunning = true;
	private static int SUPERVISOR_INTERVAL = 10000; 
	
	@Override
	public void run() {
		while(keepRunning) {
			System.out.println("StoreSupervisor - Start");
			List<StoreController> lstOver = getStoreController(State.OVERLOADED);
			if(lstOver.size() > 0) {
				List<StoreController> lstNotOver = new ArrayList<StoreController>(getStoreController(State.OVERLOADED));
				lstNotOver.addAll(getStoreController(State.LOADED));
				for(StoreController store : lstOver) {
					if(!lstNotOver.isEmpty()) {
						StoreController store_targ = lstNotOver.get(0);
						
						StoreMaster.moveProfil(store, store_targ, "");
					}
					else {
						System.out.println("StoreSupervisor - A Store is overloaded but no store available to move profiles.");
						break;
					}
				}
				
			}
			/*int ind = 1;
			for(StoreController storeCntr : StoreMaster.stores) {
				if(storeCntr.getState() == State.OVERLOADED) {
					System.out.println("StoreSupervisor - Store "+ind+" is OVERLOADED");
					// storeTarg = store to move profils.
					// profilID = profil to move.
					//StoreMaster.moveProfil(storeCntr, storeTarg, profilID);
				}
				ind++;
			}*/
			System.out.println("StoreSupervisor - Sleep");
		    try { Thread.sleep(SUPERVISOR_INTERVAL); }
		    catch (InterruptedException e) {}
		}
	}
	
	public List<StoreController> getStoreController(State state) {
		List<StoreController> ret = new ArrayList<StoreController>();
		for(StoreController storeCntr : StoreMaster.stores) {
			if(storeCntr.getState() == state)
				ret.add(storeCntr);
		}		
		return ret;
	}
	
	public void stopWork() {
		this.keepRunning = false;
	}

}
