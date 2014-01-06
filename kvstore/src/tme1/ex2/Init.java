package tme1.ex2;

import oracle.kv.*;

/**
 * Initializes the kvstore for the second exercise of tmeKVStore.
 */
public class Init{

	private final KVStore store;

	/**
	 * Runs Init
	 */
	public static void main(String args[]) {
		try {
			Init a = new Init();
			a.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Parses command line args and opens the KVStore.
	 */
	/*private Init(String[] argv) {

		// kvstore development parameters
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
	}*/

	public Init() {

		// kvstore development parameters
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";
		
		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}

	
	/*private void usage(String message) {
		System.out.println("\n" + message + "\n");
		System.out.println("usage: " + getClass().getName());
		System.out.println("\t-store <instance name> (default: kvstore) " +
				"-host <host name> (default: localhost) " +
				"-port <port number> (default: 5000)");
		System.exit(1);
	}*/

	/**
	 * Initialisation. Puts 200 products P0 to P199 classified in
	 * categories C0 to C9.
	 */
	void go() throws Exception {
		System.out.println("Initialisation...");

		for (int i = 0; i < 200; i++) {
			String major = "C" + i/20;
			String minor = "P" + i;
			String v = "1";
			System.out.println("write " + major + " " + minor + " " + v);
			store.put(Key.createKey(major, minor),
					  Value.createValue(v.getBytes()));
		}
		System.out.println("Fin d'initialisation");
		store.close();
	}
}
