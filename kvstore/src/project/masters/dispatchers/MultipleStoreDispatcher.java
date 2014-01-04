package project.masters.dispatchers;

public class MultipleStoreDispatcher extends StoreDispatcher {
	
	public MultipleStoreDispatcher(int storeNumber) {
		super(storeNumber);
	}
	
	@Override
	public int getStoreIndexForKey(Long profileKey) {
		System.out.println("storenumber " + storeNumber);
		System.out.println((profileKey % storeNumber));
		return (int) (profileKey % storeNumber);
	}

}
