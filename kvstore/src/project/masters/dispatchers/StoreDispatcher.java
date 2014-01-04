package project.masters.dispatchers;

/**
 * Maps keys to store. 
 * @author Robin Keunen
 */
public abstract class StoreDispatcher {
	
	protected final int storeNumber;
	
	public StoreDispatcher(int storeNumber) {
		this.storeNumber = storeNumber;
	}

	public abstract int getStoreIndexForKey(Long profileKey);
}
