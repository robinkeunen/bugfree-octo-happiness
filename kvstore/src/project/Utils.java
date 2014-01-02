package project;
import java.util.Random;

import oracle.kv.ValueVersion;

public class Utils {

	static Random random = new Random();

	public static Integer intFromVV(ValueVersion vs) {
		return Integer.parseInt(new String(vs.getValue().getValue()));
	}

	public static byte[] bytesFromInteger(Integer i) {
		return String.valueOf(i).getBytes();
	}

	public static String randomWord(int maxWordLength)
	{
		char[] word = new char[random.nextInt(maxWordLength)+3]; // words of length 3 through 10. (1 and 2 letter words are boring.)
		for(int j = 0; j < word.length; j++)
		{
			word[j] = (char)('a' + random.nextInt(26));
		}

		return new String(word);
	}

}
