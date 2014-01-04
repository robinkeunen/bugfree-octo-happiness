package project.masters.dispatchers;

public class SingleStoreDispatcher extends StoreDispatcher {
	
	public SingleStoreDispatcher() {
		super(1);
	}

	@Override
	public int getStoreIndexForKey(Long profileKey) {
		return 0;
	}

}
