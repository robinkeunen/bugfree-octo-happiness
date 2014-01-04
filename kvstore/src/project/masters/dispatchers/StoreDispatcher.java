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
	
	/**
	 * When an object is moved, it must be manually registered in the 
	 * dispatcher.
	 * @param profileId
	 * @param storeIndex
	 * @throws UnsupportedException thrown when the concrete dispatcher does not 
	 *  support moving profile between stores. 
	 */
	public abstract void manualMap(Long profileId, int storeIndex) throws UnsupportedException;
}
