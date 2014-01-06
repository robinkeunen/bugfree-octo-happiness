package project.masters.dispatchers;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Il retourne index = (profil mod n) où n est le nombre de stores.
 * MultipleStoreDispatcher supporte en outre la fonction manualMap.
 * Cette fonction permet d'assigner manuellement un store à un profil.
 * Lorsque le StoreMaster demande un index pour un profil, MultipleStoreDispatcher 
 * fait un lookup dans sa table de profil.
 * Si le profil est dans la table, il retourne l'index associé, sinon il retourne 
 * l'index selon la fonction indiquée plus haut.        
 *
 */
public class MultipleStoreDispatcher extends StoreDispatcher {
	
	private ConcurrentHashMap<Long, Integer> profileMap;
	
	public MultipleStoreDispatcher(int storeNumber) {
		super(storeNumber);
		
		// TODO initialize profilemap from persistent store
		// concurrency level is set to 1 to block the table on updates
		profileMap = new ConcurrentHashMap<Long, Integer>(100, (float) 0.75, 1);
	}
	
	@Override
	public int getStoreIndexForKey(Long profileKey) {
		Integer storeIndex = profileMap.get(profileKey);
		if (storeIndex == null)
			storeIndex = (int) (profileKey % storeNumber); 
		return storeIndex;
	}

	@Override
	public void manualMap(Long profileId, int storeIndex)
			throws UnsupportedException {
		profileMap.put(profileId, storeIndex);
	}

}
