package project.masters.dispatchers;

import oracle.kv.Key;

/**
 * Maps keys to store. 
 * @author Robin Keunen
 */
public interface StoreDispatcher {
	public int storeNumber = 0;
	
	public void setStoreNumber(int storeNumber);
	
	public long storeID(Key key);

	public int getStoreIndexForKey(Long profileKey);
}
