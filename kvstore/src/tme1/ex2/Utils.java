package tme1.ex2;

import oracle.kv.ValueVersion;

public class Utils {

	public static Integer intFromVV(ValueVersion vs) {
		return Integer.parseInt(new String(vs.getValue().getValue()));
	}

	public static byte[] bytesFromInteger(Integer i) {
		return String.valueOf(i).getBytes();
	}

}
