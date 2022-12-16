package controller;

public enum Padding {
	
	SMALL(10),
	BIG(2*Padding.SMALL.get());

	private final int val;
	private Padding(int val) { this.val = val; }
	public int get() { return val; }

}
