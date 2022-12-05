package util;

public enum Level {
	
	OFF  ((byte) 0),
	FATAL((byte) 1),
	ERROR((byte) 2),
	WARN ((byte) 3),
	INFO ((byte) 4),
	ALL  ((byte) 5);

	private final byte val;
	private Level(byte val) { this.val = val; }
	public int get() { return val; }
}
