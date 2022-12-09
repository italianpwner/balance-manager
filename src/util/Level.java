package util;

public enum Level {
	
	OFF  (0),
	FATAL(1),
	ERROR(2),
	WARN (3),
	INFO (4),
	DEBUG(5),
	TRACE(6),
	DATA (7);

	private final int val;
	private Level(int val) { this.val = val; }
	public int get() { return val; }
}
