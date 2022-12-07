package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.eclipse.swt.widgets.DateTime;

public class DateUtils {
	
	private static final DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	public static LocalDate convert(String text) {
		return LocalDate.parse(text, formatter);
	}
	
	public static String toString(LocalDate date) {
		return date.format(formatter);
	}
	
	public static LocalDate convert(DateTime d) {
		return LocalDate.of(
				d.getYear(), d.getMonth()+1, d.getDay()
		);
	}

}
