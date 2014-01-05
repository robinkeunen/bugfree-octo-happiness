package project.store;

import oracle.kv.stats.OperationMetrics;

public class TransactionMetrics implements OperationMetrics {

	private float averageLatencyMs;
	private int maxLatencyMs;
	private int minLatencyMs;
	private int totalRequests;
	private int totalOps;
	
	static final float FILTER_FACTOR = (float) 0.99;
	private float filteredLatency;
	
	public TransactionMetrics() {
		this.averageLatencyMs = -1;
		this.maxLatencyMs = -1;
		this.minLatencyMs = Integer.MAX_VALUE;
		this.totalRequests = 0;
		this.filteredLatency = 0;
	}
	
	public void update(int latency, int nbOperations) {
		totalRequests++;
		totalOps += nbOperations;
		
		if (latency < minLatencyMs)
			minLatencyMs = latency;
		if (latency > maxLatencyMs)
			maxLatencyMs = latency;
		
		averageLatencyMs = ((averageLatencyMs * (totalRequests - 1)) + latency) / totalRequests;
		
		// Exponential 'smoothing' filter
		filteredLatency = FILTER_FACTOR * filteredLatency + (1 - FILTER_FACTOR) * latency;
	}

	@Override
	public float getAverageLatencyMs() {
		return averageLatencyMs;
	}

	@Override
	public int getMaxLatencyMs() {
		return maxLatencyMs;
	}

	@Override
	public int getMinLatencyMs() {
		return minLatencyMs;
	}

	@Override
	public String getOperationName() {
		return "doProfileTransaction";
	}

	@Override
	public int getTotalOps() {
		return totalOps;
	}

	@Override
	public int getTotalRequests() {
		return totalRequests;
	}

	/**
	 * @return the filteredLatency
	 */
	public float getFilteredLatency() {
		return filteredLatency;
	}

}
