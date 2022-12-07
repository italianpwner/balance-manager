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
	
	public void fatal(Exception e) { 
		_log(Level.FATAL, "");
		e.printStackTrace(System.out);
		System.exit(-1);
	}
	public void error(String message) { _log(Level.ERROR, message); }
	public void warn (String message) { _log(Level.WARN , message); }
	public void info (String message) { _log(Level.INFO , message); }
	public void debug(String message) { _log(Level.DEBUG, message); }
	public void data (String message) { _log(_data , "DATA" , "\""+message); }
	public void trace(String message) { _log(_trace, "TRACE", "\t"+message); }
	
	private void _log(Level level, String message) {
		boolean enabled = _level.get() >= level.get();
		if(enabled) _print(level.toString(), message);
	}
	
	private void _log(boolean enabled, String level, String message) {
		if(enabled) _print(level, message);
	}
	
	private void _print(String level, String message) {
		SimpleDateFormat sdf =
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
		Timestamp t = new Timestamp(System.currentTimeMillis());
		
		System.out.println(String.format(
				"%-7s %s\t%s",
				"["+level.toString()+"]", sdf.format(t), message)
		);
	}
}
