package project.master;

/**
 * Avant d'être utilisé, le StoreMaster doit être configuré.
 * La configuration consiste à lui passer une liste d'instances de oracle.kv.KVStore
 * Si cette configuration n'a pas été faite avant l'instantiation, cette exception est levée.
 *
 */
public class MissingConfigurationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1679255968820638846L;

	public MissingConfigurationException(String string) {
		super(string);
	}

}
