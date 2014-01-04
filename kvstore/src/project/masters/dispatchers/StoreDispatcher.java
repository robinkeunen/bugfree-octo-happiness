package project.masters.dispatchers;

import oracle.kv.Key;

/**
 * Maps keys to store. 
 * @author Robin Keunen
 */
public interface StoreDispatcher {
		
	public void setStoreNumber(int storeNumber);

	public int getStoreIndexForKey(Long profileKey);
}
