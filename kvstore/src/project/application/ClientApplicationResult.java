package project.application;

/**
 * The purpose of this class is to encapsulates the execution
 * statistics to return it from the callable.
 * @author Robin Keunen and Clément Barbier
 */
public class ClientApplicationResult {
	private final int transactionNumber;
	private final long totalExecutionTime; 
	private final long averageTransactionTime;
	
	public ClientApplicationResult(int transactionNumber,
			long totalExecutionTime, long averageTransactionTime) {
		this.transactionNumber = transactionNumber;
		this.totalExecutionTime = totalExecutionTime;
		this.averageTransactionTime = averageTransactionTime;
	}
	/**
	 * Number of transaction executed in this ClientApplication instance.
	 * @return the transactionNumber
	 */
	public int getTransactionNumber() {
		return transactionNumber;
	}
	/**
	 * Execution time of this ClientApplication instance (nanoseconds)
	 * @return the totalExecutionTime
	 */
	public long getTotalExecutionTime() {
		return totalExecutionTime;
	}
	/**
	 * Average execution time for a transaction of 
	 * this ClientApplication instance (nanoseconds);
	 * @return the averageTransactionTime
	 */
	public long getAverageTransactionTime() {
		return averageTransactionTime;
	}
	
}
