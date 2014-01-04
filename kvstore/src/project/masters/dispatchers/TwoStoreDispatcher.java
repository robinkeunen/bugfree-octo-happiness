package project.masters.dispatchers;

import oracle.kv.Key;

public class TwoStoreDispatcher implements StoreDispatcher {

	private int storeNumber = 2;

	@Override
	public void setStoreNumber(int storeNumber) {
		if (storeNumber != 2)
			System.out.println("TwoStoreDispatcher only allows two stores.");
		this.storeNumber = 2;	}

	@Override
	public int getStoreIndexForKey(Long profileKey) {
		return (int) (profileKey%2);
	}

}
