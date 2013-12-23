package tme1.ex2;

import java.util.LinkedList;
import java.util.List;

import oracle.kv.DurabilityException;
import oracle.kv.FaultException;
import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.Operation;
import oracle.kv.OperationExecutionException;
import oracle.kv.OperationFactory;
import oracle.kv.Value;
import oracle.kv.Version;
import oracle.kv.ReturnValueVersion;

public class Transaction {
	
	private final KVStore store;
	private final OperationFactory operationFactory;
	private List<Operation> operations;

	public Transaction() {
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
		operationFactory = store.getOperationFactory();
		
		operations = new LinkedList<Operation>();
	}

	public void addPutIfVersion(Key key, Value value, Version version) {
		Operation piv = operationFactory.createPutIfVersion(
				key, value, version,
				ReturnValueVersion.Choice.NONE, true);
		operations.add(piv);
	}
	
	public void execute() throws OperationExecutionException {
		try {
			store.execute(operations);
		} catch (DurabilityException e) {
			e.printStackTrace();
		} catch (FaultException e) {
			e.printStackTrace();
		}
	}
}
