package project;

import oracle.kv.*;

/**
 * TME avec KVStore : Init
 */
public class Init{

	private final KVStore store;

	/**
	 * Runs Init
	 */
	public static void main(String args[]) {
		try {
			Init i = new Init(args);
			i.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses command line args and opens the KVStore.
	 */
	private Init(String[] argv) {

		// local store parameters
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";

		final int nArgs = argv.length;
		int argc = 0;

		while (argc < nArgs) {
			final String thisArg = argv[argc++];

			if (thisArg.equals("-store")) {
				if (argc < nArgs) {
					storeName = argv[argc++];
				} else {
					usage("-store requires an argument");
				}
			} else if (thisArg.equals("-host")) {
				if (argc < nArgs) {
					hostName = argv[argc++];
				} else {
					usage("-host requires an argument");
				}
			} else if (thisArg.equals("-port")) {
				if (argc < nArgs) {
					hostPort = argv[argc++];
				} else {
					usage("-port requires an argument");
				}
			} else {
				usage("Unknown argument: " + thisArg);
			}
		}

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}

	private void usage(String message) {
		System.out.println("\n" + message + "\n");
		System.out.println("usage: " + getClass().getName());
		System.out.println("\t-store <instance name> (default: kvstore) " +
				"-host <host name> (default: localhost) " +
				"-port <port number> (default: 5000)");
		System.exit(1);
	}

	/**
	 * Initialisation. Put 1000 profiles in the base.
	 */
	void go() throws Exception {
		System.out.println("Initialisation...");

		Key key = null;
		Value value = Value.createValue(String.valueOf(0).getBytes());
		for (long profileNumber = 0; profileNumber < 1000; profileNumber++) {
			key = Key.createKey(String.valueOf(profileNumber));
			store.put(key, value);
			Item.createRandomItem();
			System.out.println("set " + key.toString() + " to " + 0);
		}
		
		System.out.println("Initialisation... Done");
		store.close();
	}
}
