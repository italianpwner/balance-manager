package controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Point;

import util.Logger;

public class CategoriesService {

	private static CategoriesService instance;
	private Map<String,Boolean> categories;
	private int checkboxesPerLine;
	private Point size;
	private Point startPos;
	private Point increment;
	private Point nextButtonLocation;
	
	private CategoriesService() {
		categories = new HashMap<String,Boolean>();
	}
	
	static CategoriesService getInstance() {
		Logger.trace("CategoriesService >> getInstance");
		
		if(instance == null)
			instance = new CategoriesService();
		return instance;
	}
	
	Boolean isSelected(String category) {
		return categories.get(category);
	}
	
	Set<String> getSelected() {
		Logger.trace("CategoriesService >> getSelected");
		Set<String> selected = new HashSet<String>();
		for(String category: get())
			if(isSelected(category))
				selected.add(category);
		return selected;
	}
	
	Set<String> getNew() {
		Logger.trace("CategoriesService >> getNew");
		
		Set<String> newCategories = new HashSet<String>();
		for(String category: get())
			if(isSelected(category) == null) {
				newCategories.add(category);
				set(category, true);
			}
		return newCategories;
	}
	
	boolean isNew(String category) {
		Logger.trace("CategoriesService >> isNew");
		return ! categories.containsKey(category);
	}
	
	Point getNextButtonLocation() {
		computeNextButtonLocation();
		return nextButtonLocation;
	}
	
	private void computeNextButtonLocation() {	
		Logger.trace("CategoriesService >> computeNextButtonLocation");	
		int max_X = startPos.x + increment.x*(checkboxesPerLine-1);
		
		if(nextButtonLocation.x == max_X) {
			nextButtonLocation.x = startPos.x;
			nextButtonLocation.y += increment.y;
		}
		else
			nextButtonLocation.x += increment.x;
	}
	
	int size() { return categories.size(); }

	Set<String> get() { return categories.keySet(); }
	void set(String category, Boolean selected) {
		categories.put(category, selected);
	}
	
	Point getSize() { return size; }
	void perLine(int n) { checkboxesPerLine = n; }
	
	void setCheckboxSize(int x, int y) { size = new Point(x,y); }
	void setStartingLocation(int x, int y) { startPos = new Point(x,y); }
	void setIncrement(int x, int y) { increment = new Point(x,y); }
	
	void setNextButtonLocation() {
		nextButtonLocation = new Point(
				startPos.x-increment.x,
				startPos.y-increment.y);
	}
}
