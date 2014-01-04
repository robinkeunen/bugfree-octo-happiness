package project.master;

import project.store.StoreController;
import project.store.StoreController.State;

public class StoreSupervisor implements Runnable {
	
	private boolean keepRunning = true;
	private static int SUPERVISOR_INTERVAL = 10000; 
	
	@Override
	public void run() {
		while(keepRunning) {
			System.out.println("StoreSupervisor - Start");
			int ind = 1;
			for(StoreController storeCntr : StoreMaster.stores) {
				if(storeCntr.getState() == State.OVERLOADED) {
					System.out.println("StoreSupervisor - Store "+ind+" is OVERLOADED");
					// storeTarg = store to move profils.
					// profilID = profil to move.
					//StoreMaster.moveProfil(storeCntr, storeTarg, profilID);
				}
				ind++;
			}
			System.out.println("StoreSupervisor - Sleep");
		    try { Thread.sleep(SUPERVISOR_INTERVAL); }
		    catch (InterruptedException e) {}
		}
	}
	
	public void stopWork() {
		this.keepRunning = false;
	}

}
