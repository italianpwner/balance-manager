package controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.Point;

import model.Category;
import util.Logger;

public class CategoriesService {

	private static CategoriesService instance;
	private Map<String,Category> categories;
	private int checkboxesPerLine;
	private Point size;
	private Point startPos;
	private Point increment;
	private Point nextButtonLocation;
	
	private CategoriesService() {
		categories = new HashMap<String,Category>();
	}
	
	static CategoriesService getInstance() {
		Logger.trace("CategoriesService >> getInstance");
		
		if(instance == null)
			instance = new CategoriesService();
		return instance;
	}
	
	Boolean isSelected(String category) {
		return categories.get(category).isSelected();
	}
	
	String getAmount(String category) {
		return categories.get(category).getAmount().toString();
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
				updateCategories(category, true);
			}
		return newCategories;
	}
	
	boolean isNew(String category) {
		return ! categories.containsKey(category);
	}
	
	Point getNextButtonLocation() {
		computeNextButtonLocation();
		return nextButtonLocation;
	}
	
	private void computeNextButtonLocation() {
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
	
	Point getSize() { return size; }
	void perLine(int n) { checkboxesPerLine = n; }
	
	void setSize(int x, int y) { size = new Point(x,y); }
	void setStartingLocation(int x, int y) { startPos = new Point(x,y); }
	void setIncrement(int x, int y) { increment = new Point(x,y); }
	
	void setNextButtonLocation() {
		nextButtonLocation = new Point(
				startPos.x-increment.x,
				startPos.y-increment.y);
	}
	
	void updateCategories(
			String category, BigDecimal amount, Boolean selected)
	{
		if(isNew(category))
			categories.put(category,
					new Category(category,amount,selected));
		else {
			Category cat = categories.get(category);
			cat.setSelected(selected);
			cat.addAmount(amount);
			categories.put(category, cat);
		}
	}
	
	void updateCategories(String category, Boolean selected) {
		Category cat = categories.get(category);
		updateCategories(cat.getName(), new BigDecimal("0.0"), selected);
	}
	
	void updateCategories(String category, BigDecimal amount) {
		Category cat = categories.get(category);
		updateCategories(cat.getName(), amount, cat.isSelected());
	}
	
	void resetAmounts() {
		for(String name: categories.keySet()) {
			Category cat = categories.get(name);
			cat.setAmount(new BigDecimal("0.0"));
			categories.put(name, cat);
		}
	}
}
