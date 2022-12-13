package controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
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
	
	private TransactionService transactionService;
	private CategoriesService categories;
	private DateTime calendarFrom;
	private DateTime calendarTo;
	
	public ViewService() {
		
		transactionService = TransactionService.getInstance();
		categories = CategoriesService.getInstance();

		Logger.debug("ViewService: Creating table...");
		initTable();
		Logger.debug("ViewService: Table created.");
		
		initBalance();
		initDates();
		initCategories();
		addEventListeners();
		updateInterface();
	}
	
	
	private void initTable() {
		Logger.trace("ViewService >> initTable");
		
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
	
	
	private void updateInterface() {
		Logger.trace("ViewService >> updateInterface");

		LocalDate from = DateUtils.convert(textDateFrom.getText());
		LocalDate  to  = DateUtils.convert(textDateTo.getText());

		Logger.debug("Selected date range: "+
				DateUtils.toString(from) +" - "+
				DateUtils.toString(to));
		
		Logger.debug("Selected categories: {"+
				StringUtils.toString(categories.getSelected())+
		"}");
		
		
		List<Transaction> filteredList = transactionService
				.getCache(from,to);
		
		Set<String> categoriesInFilteredList =
				new HashSet<String>();
		table.setItemCount(0);
		
		for(Transaction t: filteredList) {
			
			// *****************   Update table   ****************
			if(categories.isSelected(t.getCategory())) {
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
		
		for(Button b: categories.getCheckboxes())
				b.setEnabled(
						categoriesInFilteredList
						.contains(b.getText()));
		
		Logger.debug("ViewService: Window contents updated");
	}
	
	
	private void initBalance() {
		Logger.trace("ViewService >> initBalance");
		textTotBalance.setText(
				transactionService.getTotalBalance().toString());
	}
	
	
	private void addEventListeners() {
		Logger.trace("ViewService >> addEventListeners");
		
		btnLoadNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Logger.trace("btnLoadNew: clicked");
				Logger.info("User clicked on 'btnLoadNew'");
				
				boolean theresNewData =
						transactionService.loadNewTransactions();
				if(theresNewData) {
					textTotBalance.setText(
							transactionService.getTotalBalance().toString());
					textDateFrom.setText(DateUtils.toString(
							transactionService.getFirstDate()));
					textDateTo  .setText(DateUtils.toString(
							transactionService.getLastDate()));
					
					for(String category: categories.getNew()) {
						categories.set(category, true);
						createCategoryButton(category);
					}
					
					updateInterface();
				}
			}
		});
		Logger.debug("ViewService: SelectionListener added to 'btnLoadNew'");

		
		textDateFrom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Logger.trace("textDateFrom: clicked");
				Logger.info("User clicked on 'textDateFrom'");
				openCalendar(textDateFrom, calendarFrom);
			}
		});
		Logger.debug("ViewService: MouseListener added to 'textDateFrom'");
		
	
		textDateTo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				Logger.trace("textDateTo: clicked");
				Logger.info("User clicked on 'textDateTo'");
				openCalendar(textDateTo, calendarTo);
			}
		});
		Logger.debug("ViewService: MouseListener added to 'textDateTo'");
		
		
		calendarFrom.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {	// TODO find a better event
				Logger.trace("calendarFrom: clicked");
				Logger.info("User selected from 'calendarFrom'"+
							"and updated 'textDateFrom'");
				closeCalendar(textDateFrom, calendarFrom);
			}
		});
		Logger.debug("ViewService: SelectionListener added to '_calendarFrom'");
		
		
		calendarTo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {	// TODO find a better event
				Logger.trace("calendarTo: clicked");
				Logger.info("User selected from 'calendarTo'"+
							"and updated 'textDateTo'");
				closeCalendar(textDateTo, calendarTo);
			}
		});
		Logger.debug("ViewService: SelectionListener added to '_calendarTo'");
	}
	
	private void initDates() {
		Logger.trace("ViewService >> initDates");
		
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
		
		LocalDate firstDate = transactionService.getFirstDate();
		LocalDate lastDate  = transactionService.getLastDate();
		
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
	
	private void initCategories() {
		Logger.trace("ViewService >> initCategories");
		
		for(Transaction t: transactionService.getCache())
			categories.set(t.getCategory(), true);
		
		for(String name : categories.get())
			createCategoryButton(name);
	}
	
	
	void createCategoryButton(String name) {
		Logger.trace("ViewService >> createCategoryButton");
		Button b = new Button(shell, SWT.CHECK);
		
		int[] position = categories.getNextButtonCoords();
		b.setBounds(position[0], position[1], 70, 20);
		
		b.setText(name);
		b.setSelection(categories.isSelected(name));
		
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = b.getSelection();
				
				String action = (selected?"":"un")+"checked";
				Logger.trace("btnCheckbox"+name+": "+action);
				Logger.info("User "+action+" CheckBox '"+name+"'");
				
				categories.set(name,selected);
				updateInterface();
			}
		});
		
		categories.addButton(b);
	}
	
	
	private void openCalendar(Text date, DateTime calendar) {
		Logger.trace("ViewService >> openCalendar");
		    date.setVisible(false);
		calendar.setVisible(true);
	}
	
	private void closeCalendar(Text date, DateTime calendar) {
		Logger.trace("ViewService >> closeCalendar");
		    date.setVisible(true);
		calendar.setVisible(false);
		
		date.setText(DateUtils.convert(calendar));
		updateInterface();
	}
}
