package project.dispatchers;

import oracle.kv.Key;

public class SingleStoreDispatcher implements StoreDispatcher {

	@Override
	public void setStoreNumber(int storeNumber) {
		System.out.println("SingleStoreDispatcher only allows a single store");
		storeNumber = 1;
	}

	@Override
	public long storeID(Key key) {
		return 0;
	}

}
