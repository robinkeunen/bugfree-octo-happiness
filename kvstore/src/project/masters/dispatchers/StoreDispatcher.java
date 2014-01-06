package project.masters.dispatchers;

/**
 * Les StoreDispatcher servent à attribuer un contrôleur à chaque profil.
 * La fonctiongetStoreIndexForKey retourne un entier qui servira d'index dans la liste de contrôleurs du StoreMaster .
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
