package tests;

import project.ConfigsServer;
import project.master.StoreMaster;

public class LoadBalancingTest {
	public static void main(String[] args) {
		try {
			LoadBalancingTest t = new LoadBalancingTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public LoadBalancingTest() {
		StoreMaster.setKVStores(ConfigsServer.getServersStores());
	}

}
