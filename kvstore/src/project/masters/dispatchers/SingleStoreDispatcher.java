package project.masters.dispatchers;

/**
 * Utilisé dans l'étape 1 de la résolution.
 * Retourne 0 quelque soit le profil.
 *
 */
public class SingleStoreDispatcher extends StoreDispatcher {

	public SingleStoreDispatcher() {
		super(1);
	}

	@Override
	public int getStoreIndexForKey(Long profileKey) {
		return 0;
	}
	
	@Override
	public void manualMap(Long profileId, int storeIndex) throws UnsupportedException {
		throw new UnsupportedException("Manual map not supported in SingleStoreDispatcher");
	}
}
