package project.masters.dispatchers;

import oracle.kv.Key;

public class SingleStoreDispatcher implements StoreDispatcher {
	private int storeNumber = 1;
	
	@Override
	public void setStoreNumber(int storeNumber) {
		if (storeNumber != 1)
			System.out.println("SingleStoreDispatcher only allows a single store");
		this.storeNumber = 1;
	}

	@Override
	public int getStoreIndexForKey(Long profileKey) {
		return 0;
	}

}
