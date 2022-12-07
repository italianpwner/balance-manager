package util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Logger {

	private static Logger instance;
	private static Level   _level;
	private static boolean _trace;
	private static boolean _data;
	
	public static Logger getInstance() {
		return getInstance(_level,_trace,_data);
	}
	
	private Logger() {}
	public static Logger getInstance(Level level,
			boolean trace, boolean data)
	{
		if(instance == null)
			instance = new Logger();
		
		_level = level;
		_trace = trace;
		_data  = data;
		
		return instance;
	}
	
	public void fatal(String message) { 
		log(Level.FATAL, message);
		System.exit(-1);
	}
	public void error(String message) { log(Level.ERROR, message); }
	public void warn (String message) { log(Level.WARN , message); }
	public void info (String message) { log(Level.INFO , message); }
	public void debug(String message) { log(Level.DEBUG, message); }
	public void data (String message) { log(_data , "DATA" , "\""+message); }
	public void trace(String message) { log(_trace, "TRACE", "\t"+message); }
	
	private void log(Level level, String message) {
		boolean enabled = _level.get() >= level.get();
		if(enabled) print(level.toString(), message);
	}
	
	private void log(boolean enabled, String level, String message) {
		if(enabled) print(level, message);
	}
	
	private void print(String level, String message) {
		SimpleDateFormat sdf =
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Timestamp t = new Timestamp(System.currentTimeMillis());
		
		System.out.println(String.format(
				"%-7s %s\t%s",
				"["+level.toString()+"]", sdf.format(t), message)
		);
	}
}
