package util;

import java.util.Set;

public class StringUtils {
	
	public static String toString(Set<String> set) {
		String res = "";
		if(set.size() == 0)
			return res;
		
		for(String s: set)
			res += s+", ";
		return  res.substring(0, res.length()-2);
	}

}
