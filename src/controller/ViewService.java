package controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableItem;

import model.Transaction;
import util.DateUtils;
import util.Logger;
import util.StringUtils;

public class ViewService extends view.MainWindow {
	
	static Logger logger = Logger.getInstance();
	
	static TransactionService service;
	private static List<Button> categoryCheckboxes;
	private static Set<String> selectedCategories;
	
	
	public static void init() {
		service = TransactionService.getInstance();
		categoryCheckboxes = new LinkedList<Button>();
		selectedCategories = service.getCategories();
	}
	
	
	public static void updateInterface(boolean drawCheckboxes) {
		logger.trace("ViewService >> updateInterface");

		LocalDate from = DateUtils.convert(dateTimeFrom);
		LocalDate  to  = DateUtils.convert(dateTimeTo);

		logger.debug("Selected date range: "+
				DateUtils.toString(from) +" - "+
				DateUtils.toString(to));
		
		logger.debug("Selected categories: {"+
				StringUtils.toString(selectedCategories)+
		"}");
		
		
		List<Transaction> list = service
				.getCache(selectedCategories,from,to);
		_updateTable(list);
		
		if(drawCheckboxes) {
			Set<String> set = new HashSet<String>();
			for(Transaction t: list)
				set.add(t.getCategory());
			drawCheckboxes(set);
		}
		
		logger.debug("ViewService: Window contents updated");
	}
	
	
	private static void _updateTable(List<Transaction> list) {
		logger.trace("ViewService >> updateTable");
		
		table.setItemCount(0);
		for(int i=0; i<list.size(); i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			Transaction t = list.get(i);
			
			item.setText(1, t.getId().toString());
			item.setText(2, t.getAmount());
			item.setText(3, t.getDate());
			item.setText(4, t.getCategory());
			item.setText(5, t.getDescription());
		}
	}
	
	
	public static void initBalance() {
		logger.trace("ViewService >> initBalance");
		textTotBalance.setText(
				service.getTotalBalance().toString());
	}
	
	
	public static void addEventListeners() {
		logger.trace("ViewService >> addEventListeners");
		
		btnLoadNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.trace("btnLoadNew: clicked");
				logger.info("User clicked on 'btnLoadNew'");
				
				boolean theresNewData =
						service.loadNewTransactions();
				if(theresNewData) {
					textTotBalance.setText(
							service.getTotalBalance().toString());
					updateInterface(true);
				}
			}
		});
		logger.debug("ViewService: Added listener to Button 'btnLoadNew'");

		
		dateTimeFrom.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.trace("dateTimeFrom: updated");
				logger.info("User updated 'dateTimeFrom'");
				updateInterface(true);
			}
		});
		logger.debug("Added listener to DateTime 'dateTimeFrom'");
		
	
		dateTimeTo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				logger.trace("dateTimeTo: updated");
				logger.info("User updated 'dateTimeTo'");
				updateInterface(true);
			}
		});	
		logger.debug("ViewService: Added listener to DateTime 'dateTimeTo'");
	}
	
	public static void initDateTimes() {
		logger.trace("ViewService >> initDateTimes");
		
		LocalDate firstDate = service.getFirstDate();
		dateTimeFrom.setDate(
				firstDate.getYear(),
				firstDate.getMonthValue()-1,
				firstDate.getDayOfMonth());

		LocalDate lastDate = service.getLastDate();
		dateTimeTo.setDate(
				lastDate.getYear(),
				lastDate.getMonthValue()-1,
				lastDate.getDayOfMonth());
	}
	
	public static void drawCheckboxes(Set<String> set) {
		logger.trace("ViewService >> drawCheckboxes");
		
		for(Button b: categoryCheckboxes)
			b.dispose();
		
		String[] categories = new String[set.size()];
		categories = set.toArray(categories);
		
		// {start,increment}
		int[] x = {585,135};
		int[] y = { 70, 20};
		
		int num_cat = categories.length;
		int i_max = num_cat/3 +
				(num_cat%3 != 0 ? 1 : 0);
		int j_max = 3;
		int k = 0;
		
		for(int i=0; i<i_max; i++) {
			for(int j=0; j<j_max && k<num_cat; j++, k++) {
				Button b = new Button(shell, SWT.CHECK);
				b.setBounds(
						x[0] + j*x[1],
						y[0] + i*y[1],
						111, 20);
				
				String name = categories[3*i + j];
				b.setText(name);
				b.setSelection(true);
				
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						String category = b.getText();
						boolean selected = b.getSelection();
						
						String action = (selected?"":"un")+"checked";
						logger.trace("btnCheckbox"+category+": "+action);
						logger.info("User "+action+" CheckBox '"+category+"'");
						
						if(selected) selectedCategories.add(category);
						else selectedCategories.remove(category);
						
						updateInterface(false);
					}
				});
				
				selectedCategories.add(name);
				categoryCheckboxes.add(b);
			}
		}
	}
}
