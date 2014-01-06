package project.masters.dispatchers;

/**
 * Utilisé dans l'étape 2 de la résolution.
 * Retourne 0 pour les profils pairs et 1 pour les profils impairs.
 *
 */
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

	@Override
	public void manualMap(Long profileId, int storeIndex) throws UnsupportedException {
		throw new UnsupportedException("Manual map not supported in TwoStoreDispatcher");
		
	}

}
