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
			StoreMaster.stores.get(0).setState(State.OVERLOADED);
			StoreMaster.stores.get(1).setState(State.UNDERLOADED);
			// DEBUG
			List<StoreController> lstOver = getStoreController(State.OVERLOADED);
			if(lstOver.size() > 0) {
				List<StoreController> lstNotOver = new ArrayList<StoreController>(getStoreController(State.OVERLOADED));
				lstNotOver.addAll(getStoreController(State.LOADED));
				for(StoreController store : lstOver) {
					if(!lstNotOver.isEmpty()) {
						StoreController store_targ = lstNotOver.get(0);
						String profilID = getMostItemsProfil(store);
						if(profilID != null)
							System.out.println("StoreSupervisor - Profil selected : "+profilID);
						else
							System.out.println("StoreSupervisor - No Profil selected");
						// TODO : profil avec le + d'item
						// StoreMaster.moveProfil(store, store_targ, "");
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
		for(StoreController storeCntr : StoreMaster.stores) {
			if(storeCntr.getState() == state)
				ret.add(storeCntr);
		}		
		return ret;
	}
	
	public String getMostItemsProfil(StoreController storeCntr) {
		Long profilID = storeCntr.getMonitor().getProfilMaxItems();
		if(profilID != -1)
			return profilID.toString();
		else
			return null;
	}
	
	public void stopWork() {
		this.keepRunning = false;
	}

}
