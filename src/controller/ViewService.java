package controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import model.Transaction;
import util.DateUtils;
import util.Logger;
import util.StringUtils;

public class ViewService extends view.MainWindow {
	
	static Logger logger = Logger.getInstance();
	
	static TransactionService service;
	static Map<String,Boolean> selectedCategories = new HashMap<String,Boolean>();
	private static List<Button> categoryCheckboxes = new LinkedList<Button>();
	private static DateTime calendarFrom;
	private static DateTime calendarTo;
	
	
	public static void init() {

		service = TransactionService.getInstance();
		
		logger.debug("ViewService: Creating table...");
		initTable();
		logger.debug("ViewService: Table created.");
		
		initBalance();
		initDates();
		initCategories();
		addEventListeners();
		updateInterface();
	}
	
	
	private static void initTable() {
		logger.trace("ViewService >> initTable");
		
		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);	
		table.setBounds(10, 10, 555, 630);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		int[] width = {
				0,			30,			80,
				90,			90,			260
		};
		String[] text  = {
				"",			"Id",		"Amount",
				"Date",		"Category",	"Description"
		};
		
		for(int i=0; i<width.length; i++) {
			TableColumn col = new TableColumn(table, SWT.NONE);
			col.setResizable(false);
			col.setWidth(width[i]);
			col.setText(text[i]);
		}
	}
	
	
	private static Set<String> getSelectedCategories() {
		Set<String> res = new HashSet<String>();
		for(String category: selectedCategories.keySet())
			if(selectedCategories.get(category))
				res.add(category);
		return res;
	}
	
	
	private static void updateInterface() {
		logger.trace("ViewService >> updateInterface");

		LocalDate from = DateUtils.convert(textDateFrom.getText());
		LocalDate  to  = DateUtils.convert(textDateTo.getText());

		logger.debug("Selected date range: "+
				DateUtils.toString(from) +" - "+
				DateUtils.toString(to));
		
		logger.debug("Selected categories: {"+
				StringUtils.toString(getSelectedCategories())+
		"}");
		
		
		List<Transaction> filteredList = service
				.getCache(from,to);
		
		Set<String> categoriesInFilteredList =
				new HashSet<String>();
		table.setItemCount(0);
		
		for(Transaction t: filteredList) {
			
			// *****************   Update table   ****************
			if(selectedCategories.get(t.getCategory())) {
				TableItem item = new TableItem(table, SWT.NONE);
				
				item.setText(1, t.getId().toString());
				item.setText(2, t.getAmount());
				item.setText(3, t.getDate());
				item.setText(4, t.getCategory());
				item.setText(5, t.getDescription());
			}
			
			// **************   Update checkboxes   **************
			categoriesInFilteredList.add(t.getCategory());
		}
		
		for(Button b: categoryCheckboxes)
				b.setEnabled(
						categoriesInFilteredList
						.contains(b.getText()));
		
		logger.debug("ViewService: Window contents updated");
	}
	
	
	private static void initBalance() {
		logger.trace("ViewService >> initBalance");
		textTotBalance.setText(
				service.getTotalBalance().toString());
	}
	
	
	private static void addEventListeners() {
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
					textDateFrom.setText(DateUtils.toString(
							service.getFirstDate()));
					textDateTo  .setText(DateUtils.toString(
							service.getLastDate()));
					updateInterface(true);
				}
			}
		});
		logger.debug("ViewService: SelectionListener added to 'btnLoadNew'");

		
		textDateFrom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				logger.trace("textDateFrom: clicked");
				logger.info("User clicked on 'textDateFrom'");
				openCalendar(textDateFrom, calendarFrom);
			}
		});
		logger.debug("ViewService: MouseListener added to 'textDateFrom'");
		
	
		textDateTo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				logger.trace("textDateTo: clicked");
				logger.info("User clicked on 'textDateTo'");
				openCalendar(textDateTo, calendarTo);
			}
		});
		logger.debug("ViewService: MouseListener added to 'textDateTo'");
		
		
		calendarFrom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {	// TODO find a better event
				logger.trace("calendarFrom: clicked");
				logger.info("User selected from 'calendarFrom'"+
							"and updated 'textDateFrom'");
				closeCalendar(textDateFrom, calendarFrom);
			}
		});
		logger.debug("ViewService: SelectionListener added to '_calendarFrom'");
		
		
		calendarTo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {	// TODO find a better event
				logger.trace("calendarTo: clicked");
				logger.info("User selected from 'calendarTo'"+
							"and updated 'textDateTo'");
				closeCalendar(textDateTo, calendarTo);
			}
		});
		logger.debug("ViewService: SelectionListener added to '_calendarTo'");
	}
	
	private static void initDates() {
		logger.trace("ViewService >> initDates");
		
		// ====================   Bounds definition   ====================
		java.util.Map<String, Integer> bounds = 
				new java.util.HashMap<String, Integer>();

		// **********   Width and height   **********
		bounds.put("txt_w", 102);
		bounds.put("cal_w", 225);
		bounds.put("lbl_F_w", 40);
		bounds.put("lbl_T_w", 20);
		
		bounds.put("txt_h",  28);		
		bounds.put("cal_h", 145);		
		bounds.put("lbl_h",  20);

		// **************   X and Y   **************
		int padding_x = 5;
		int padding_y = 5;
		int middle_padding = 50;
		
		bounds.put("lbl_F_x", 585);
		bounds.put("lbl_y"  , 150);

		bounds.put("date_y",   bounds.get("lbl_y") - padding_y);
		
		bounds.put("date_F_x", bounds.get("lbl_F_x") + bounds.get("lbl_F_w") + padding_x);
		bounds.put("lbl_T_x", bounds.get("date_F_x") + bounds.get("cal_w") + middle_padding);
		bounds.put("date_T_x", bounds.get("lbl_T_x") + bounds.get("lbl_T_w") + padding_x);
		
		
		// ====================   Instantiation   ====================
		Label lblFrom = new Label(shell, SWT.NONE);
		Label lblTo   = new Label(shell, SWT.NONE);
		
		textDateFrom = new Text(shell, SWT.BORDER);
		textDateTo   = new Text(shell, SWT.BORDER);
		
		calendarFrom = new DateTime(shell, SWT.CALENDAR);
		calendarTo   = new DateTime(shell, SWT.CALENDAR);
		
		calendarFrom.setVisible(false);
		calendarTo  .setVisible(false);

		// ====================   Initialization   ====================
		lblFrom.setText("From");
		lblTo  .setText("To");
		
		LocalDate firstDate = service.getFirstDate();
		LocalDate lastDate  = service.getLastDate();
		
		textDateFrom.setText(DateUtils.toString(firstDate));
		textDateTo  .setText(DateUtils.toString(lastDate));
		
		calendarFrom.setDate(
				firstDate.getYear(),
				firstDate.getMonthValue()-1,
				firstDate.getDayOfMonth());
		calendarTo  .setDate(
				lastDate.getYear(),
				lastDate.getMonthValue()-1,
				lastDate.getDayOfMonth());
		
		// ====================   Bounds setting   ====================
		Rectangle rect = new Rectangle(
				bounds.get("date_F_x"),
				bounds.get("date_y"),
				bounds.get("txt_w"),
				bounds.get("txt_h"));
		textDateFrom.setBounds(rect);

		rect.x = bounds.get("date_T_x");
		textDateTo.setBounds(rect);
		
		rect.width  = bounds.get("cal_w");
		rect.height = bounds.get("cal_h");
		calendarTo.setBounds(rect);
		
		rect.x = bounds.get("date_F_x");
		calendarFrom.setBounds(rect);
		
		rect.y = bounds.get("lbl_y");
		rect.height = bounds.get("lbl_h");
		
		rect.x = bounds.get("lbl_F_x");
		rect.width = bounds.get("lbl_F_w");
		lblFrom.setBounds(rect);
		
		rect.x = bounds.get("lbl_T_x");
		rect.width = bounds.get("lbl_T_w");
		lblTo.setBounds(rect);
	}
	
	private static void initCategories() {
		logger.trace("ViewService >> initCategories");
		
		for(Transaction t: service.getCache())
			selectedCategories.put(t.getCategory(), true);
		
		String[] categories = new String[selectedCategories.size()];
		categories = selectedCategories.keySet().toArray(categories);
		
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
				b.setSelection(selectedCategories.get(name));
				
				b.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						boolean selected = b.getSelection();
						
						String action = (selected?"":"un")+"checked";
						logger.trace("btnCheckbox"+name+": "+action);
						logger.info("User "+action+" CheckBox '"+name+"'");
						
						selectedCategories.put(name,selected);
						updateInterface();
					}
				});
				
				categoryCheckboxes.add(b);
			}
		}
	}
	
	
	private static void openCalendar(Text date, DateTime calendar) {
		logger.trace("ViewService >> openCalendar");
		    date.setVisible(false);
		calendar.setVisible(true);
	}
	
	private static void closeCalendar(Text date, DateTime calendar) {
		logger.trace("ViewService >> closeCalendar");
		    date.setVisible(true);
		calendar.setVisible(false);
		
		date.setText(DateUtils.convert(calendar));
		updateInterface();
	}
}
