package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
	
	private static final DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public static LocalDate convert(String text) {
		return LocalDate.parse(text, formatter);
	}
	
	public static String convert(LocalDate date) {
		return date.format(formatter);
	}

}
