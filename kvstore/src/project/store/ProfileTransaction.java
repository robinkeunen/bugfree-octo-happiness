package project.store;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import oracle.kv.DurabilityException;
import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Operation;
import oracle.kv.OperationExecutionException;
import oracle.kv.OperationFactory;
import oracle.kv.Value;
import project.Item;

/**
 * ProfileTransaction  est créé par le contrôleur pour créer une transaction atomique.
 * Cette classe est instanciée pour un profil donné, le contrôleur lui ajoute des opérations et lance l'exécution.
 *
 */
public class ProfileTransaction {
	
	private final long profileId;
	private final KVStore store;
	private final OperationFactory operationFactory;
	private List<Operation> operations;
	private int latencyMs;

	public ProfileTransaction(KVStore store, long profileID) {
		this.store = store;
		this.profileId = profileID;
		operationFactory = store.getOperationFactory();
		operations = new LinkedList<Operation>();
		this.latencyMs = 0;
	}
	
	public void addPutOperation(Item item) {
		Key key = Key.createKey("P" + String.valueOf(profileId), "I" + item.getItemId());
		Value value = null;
		try {
			value = Value.createValue(item.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Operation put = operationFactory.createPut(key, value);
		operations.add(put);
	}
	
	public void addPutOperation(Key key, Value value) {
		Operation put = operationFactory.createPut(key, value);
		operations.add(put);		
	}
	
	public void execute() throws OperationExecutionException {
		try {
			long start = System.currentTimeMillis();
			store.execute(operations);
			latencyMs = (int) (System.currentTimeMillis() - start);
			
		} catch (DurabilityException e) {
			e.printStackTrace();
		} catch (FaultException e) {
			e.printStackTrace();
		}
	}

	public void accept(StoreMonitor monitor) {
		monitor.updateTransactionMetrics(latencyMs, operations.size());
	}
	
	
}
