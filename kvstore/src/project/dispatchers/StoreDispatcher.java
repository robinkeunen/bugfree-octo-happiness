package project.dispatchers;

import oracle.kv.Key;

/**
 * Maps keys to store. 
 * @author Robin Keunen
 */
public interface StoreDispatcher {
	public int storeNumber = 1;
	
	public void setStoreNumber(int storeNumber);
	
	public long storeID(Key key);
}
