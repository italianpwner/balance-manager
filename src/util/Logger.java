package util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Logger {

	private static Level _level;

	public Logger(Level level) {
		set(level);
	}
	
	public static void set(Level level) {
		_level = level;
	}

	public static void fatal(String message, Exception e) { 
		_log(Level.FATAL, message);
		e.printStackTrace(System.out);
		System.exit(-1);
	}
	public static void error(String message) { _log(Level.ERROR, message); }
	public static void warn (String message) { _log(Level.WARN , message); }
	public static void info (String message) { _log(Level.INFO , message); }
	public static void debug(String message) { _log(Level.DEBUG, message); }
	public static void trace(String message) { _log(Level.TRACE, "\t"+message); }
	public static void data (String message) { _log(Level.DATA , "\""+message+"\""); }
	
	private static void _log(Level level, String message) {
		boolean enabled = _level.get() >= level.get();
		if(enabled) _print(level.toString(), message);
	}
	
	// TODO write to file instead of System.out
	private static void _print(String level, String message) {
		SimpleDateFormat sdf =
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Timestamp t = new Timestamp(System.currentTimeMillis());
		
		System.out.println(String.format(
				"%-7s %s\t%s",
				"["+level.toString()+"]", sdf.format(t), message)
		);
	}
}
