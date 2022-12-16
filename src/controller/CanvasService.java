package controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Display;

import util.Logger;

public class CanvasService {
	
	private static CanvasService instance;
	private List<BigDecimal> snapshots;
	private BigDecimal max;
	private BigDecimal min;
	private BigDecimal step;
	private int height;
	
	private CanvasService() {
		snapshots = new LinkedList<BigDecimal>();
		clear();
	}
	
	static CanvasService getInstance() {
		if(instance == null)
			instance = new CanvasService();
		return instance;
	}
	
	void addSnapshot(String amount) {
		Logger.trace("CanvasService >> addSnapshot");
		BigDecimal last = snapshots
				.get(snapshots.size()-1);
		BigDecimal newBalance = last.add(
				new BigDecimal(amount));
		snapshots.add(newBalance);
		
		if(newBalance.compareTo(max) == 1)
			max = newBalance;
		else if(newBalance.compareTo(min) == -1)
			min = newBalance;
	}
	
	void paintCanvas(GC gc, Display display) {
		// TODO remove somma iniziale
		for(int i=1; i<snapshots.size()-1; i++) {
			int x1 = step.intValue() * (i-1);	int y1 = rescaleY(i-1);
			int x2 = step.intValue() * (i-0);	int y2 = rescaleY(i-0);
			
			// TODO don't draw each iteration
			configureLine(SWT.LINE_SOLID, 4, SWT.COLOR_RED, gc, display);
			gc.drawLine(x1, y1, x2, y2);
			
			configureLine(SWT.LINE_DASH, 1, SWT.COLOR_BLACK, gc, display);
			gc.drawLine(x2, 0, x2, height);
		}
	}
	
	private int rescaleY(int i) {
		return max.subtract(snapshots.get(i))
				.multiply(new BigDecimal(height))
				.divide(max.subtract(min), 3,
						RoundingMode.HALF_EVEN)
				.intValue();
	}
	
	private void configureLine(
			int style, int width, int color,
			GC gc, Display display)
	{
		gc.setLineStyle(style);
		gc.setLineWidth(width);
		gc.setForeground(display
				.getSystemColor(color));
	}
	
	BigDecimal getMax() { return max; }
	BigDecimal getMin() { return min; }
	void setHeight(int h) { height = h; }
	
	void clear() { 
		Logger.trace("CanvasService >> clear");
		
		snapshots.clear();
		BigDecimal zero = new BigDecimal(0);
		snapshots.add(zero);
		max = zero;
		min = zero;
	}
	
	void setStep(int x) {
		Logger.trace("CanvasService >> setStep");
		step = new BigDecimal(x).divide(
				new BigDecimal(snapshots.size()-2),
				RoundingMode.HALF_EVEN);
	}
}
