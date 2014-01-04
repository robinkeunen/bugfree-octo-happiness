package project.masters.dispatchers;

public class TwoStoreDispatcher extends StoreDispatcher {

	public TwoStoreDispatcher() {
		super(2);
	}
	
	public TwoStoreDispatcher(int storeNumber) {
		super(2);
	}

	@Override
	public int getStoreIndexForKey(Long profileKey) {
		return (int) (profileKey%2);
	}

}
