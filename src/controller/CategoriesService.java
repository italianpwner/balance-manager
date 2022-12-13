package controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.widgets.Button;

public class CategoriesService {

	private static CategoriesService instance;
	private Map<String,Boolean> categories;
	private List<Button> checkboxes;
	private int perLine;
	private int startPosX;
	private int startPosY;
	private int incrementX;
	private int incrementY;
	private int[] nextButtonCoords;
	
	private CategoriesService() {
		checkboxes = new LinkedList<Button>();
		categories = new HashMap<String,Boolean>();
		
		perLine = 4;
		
		startPosX = 585;
		startPosY =  70;
		
		incrementX = 100;
		incrementY =  20;
		
		nextButtonCoords = new int[] {
				startPosX-incrementX,
				startPosY-incrementY
		};
	}
	
	static CategoriesService getInstance() {
		if(instance == null)
			instance = new CategoriesService();
		return instance;
	}
	
	boolean isSelected(String category) {
		return categories.get(category);
	}
	
	Set<String> getSelected() {
		Set<String> selected = new HashSet<String>();
		for(String category: get())
			if(isSelected(category))
				selected.add(category);
		return selected;
	}
	
	Set<String> getNew() {
		Set<String> newCategories = new HashSet<String>();
		for(String category: get())
			if(category == null)
				newCategories.add(category);
		return newCategories;
	}
	
	boolean isNew(String category) {
		return ! categories.containsKey(category);
	}
	
	void addButton(Button b) { checkboxes.add(b); }
	
	int[] getNextButtonCoords() {
		computeNextButtonCoords();
		return nextButtonCoords;
	}
	
	private void computeNextButtonCoords() {		
		int max_X = startPosX + incrementX*(perLine-1);
		
		if(nextButtonCoords[0] == max_X) {
			nextButtonCoords[0] = startPosX;
			nextButtonCoords[1] += incrementY;
		}
		else
			nextButtonCoords[0] += incrementX;
	}
	
	int size() { return categories.size(); }
	
	List<Button> getCheckboxes() { return checkboxes; }

	Set<String> get() { return categories.keySet(); }
	void set(String category, Boolean selected) {
		categories.put(category, selected);
	}
}
