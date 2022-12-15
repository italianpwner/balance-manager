package controller;

import java.awt.Toolkit;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import model.Transaction;
import util.DateUtils;
import util.Logger;
import util.StringUtils;

public class ViewService extends view.MainWindow {
		
	private TransactionService service;
	private CategoriesService categories;
	
	public ViewService() {

		instantiateWidgets();
		
		categories = CategoriesService.getInstance();
		setWidgetsSize();
		setWidgetsLocation();

		service = TransactionService.getInstance();
		configureWidgets();
		
		initCategories();
		addEventListeners();
		updateInterface();
	}
	
	private void instantiateWidgets() {
		Logger.trace("ViewService >> instantiateWidgets");

		shell = new Shell(); // TODO review SWT styles
		
		btnLoadNew = new Button(shell, SWT.NONE);
		lblTotal   = new Label (shell, SWT.NONE);
		lblFrom    = new Label (shell, SWT.NONE);
		lblTo      = new Label (shell, SWT.NONE);

		textTotal	 = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		textDateFrom = new Text(shell, SWT.BORDER);
		textDateTo   = new Text(shell, SWT.BORDER);
		
		calendarFrom = new DateTime(shell, SWT.CALENDAR);
		calendarTo   = new DateTime(shell, SWT.CALENDAR);

		table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		for(int i=0; i<NUM_COLUMNS; i++)
			new TableColumn(table, SWT.NONE);
	}
	
	
	private void setWidgetsSize() {
		Logger.trace("ViewService >> setWidgetsSize");

		Point p = new Point(0,0);
		java.awt.Dimension screen = Toolkit
				.getDefaultToolkit()
				.getScreenSize();
		
		shell.setSize(
				(int)screen.getWidth(),
				(int)screen.getHeight()
		);
		
		table	  .setSize(600, 630);
		btnLoadNew.setSize(140,  30);
		textTotal .setSize( 78,  20);
		
		p.y = 20;
		lblTotal.setSize(90, p.y);
		lblFrom	.setSize(30, p.y);
		lblTo	.setSize(15, p.y);

		p.x = 70; p.y = 20;
		textDateFrom.setSize(p);
		textDateTo	.setSize(p);
		
		p.x = 225; p.y = 145;
		calendarFrom.setSize(p);
		calendarTo	.setSize(p);
		
		int[] column_width = {0, 30, 80, 90, 90, 260};
		for(int i=0; i<NUM_COLUMNS; i++)
			table.getColumn(i).setWidth(column_width[i]);
		
		categories.setCheckboxSize(70, 20);
	}

	
	private void setWidgetsLocation() {
		Logger.trace("ViewService >> setWidgetsLocation");
		
		int smallPadding = 10;
		int bigPadding  = 2 * smallPadding;
		categories.setIncrement(100, 20);
		categories.perLine(4);
		
		table.setLocation(
				smallPadding,
				smallPadding);
		
		lblTotal.setLocation(
				table.getLocation().x
				+ table.getSize().x
				+ bigPadding,
				smallPadding
				+ (btnLoadNew.getSize().y
					- lblTotal.getSize().y) / 2
				+ lblTotal.getSize().y / 8);
		
		textTotal.setLocation(
				lblTotal.getLocation().x
				+ lblTotal.getSize().x
				+ bigPadding,
				smallPadding
				+ (btnLoadNew.getSize().y
					- lblTotal.getSize().y) / 2);
		
		btnLoadNew.setLocation(
				textTotal.getLocation().x
				+ textTotal.getSize().x
				+ bigPadding,
				smallPadding);
		
		lblFrom.setLocation(
				lblTotal.getLocation().x,
				lblTotal.getLocation().y
				+ lblTotal.getSize().y
				+ bigPadding
				+ lblFrom.getSize().y / 8);
		
		textDateFrom.setLocation(
				lblFrom.getLocation().x
				+ lblFrom.getSize().x
				+ smallPadding,
				lblFrom.getLocation().y
				- lblFrom.getSize().y / 8);
		
		calendarFrom.setLocation(
				textDateFrom.getLocation().x,
				textDateFrom.getLocation().y);

		lblTo.setLocation(
				calendarFrom.getLocation().x
				+ calendarFrom.getSize().x
				+ smallPadding,
				lblFrom.getLocation().y);
		
		textDateTo.setLocation(
				lblTo.getLocation().x
				+ lblTo.getSize().x
				+ smallPadding,
				textDateFrom.getLocation().y);
		
		calendarTo.setLocation(
				textDateTo.getLocation().x,
				textDateTo.getLocation().y);
		
		categories.setStartingLocation(
				lblFrom.getLocation().x,
				textDateFrom.getLocation().y
				+ 2*textDateFrom.getSize().y
				+ bigPadding);
		
		categories.setNextButtonLocation();
	}
	
	
	private void configureWidgets() {
		Logger.trace("ViewService >> configureWidgets");
		
		shell.setText("Balance Manager");
		shell.setMaximized(true);
		
		btnLoadNew.setText("Load new transactions");
		lblTotal.setText("Total balance (€):");
		lblFrom.setText("From");
		lblTo.setText("To");
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		String    balance   = service.getTotalBalance().toString();
		LocalDate firstDate = service.getFirstDate();
		LocalDate lastDate  = service.getLastDate();
		
		textTotal.setText(balance);
		textTotal.setEditable(false);
		
		textDateFrom.setText(DateUtils.toString(firstDate));
		textDateTo  .setText(DateUtils.toString(lastDate));

		calendarFrom.setVisible(false);
		calendarFrom.setDate(
				firstDate.getYear(),
				firstDate.getMonthValue()-1,
				firstDate.getDayOfMonth());

		calendarTo.setVisible(false);
		calendarTo.setDate(
				lastDate.getYear(),
				lastDate.getMonthValue()-1,
				lastDate.getDayOfMonth());
		
		
		String[] column_text  = {
				"", "Id", "Amount", "Date",
				"Category", "Description"
		};
		for(int i=0; i<NUM_COLUMNS; i++) {
			TableColumn col = table.getColumn(i);
			
			col.setResizable(false);
			col.setText(column_text[i]);
		}
	}
	
	
	private void updateInterface() {
		Logger.trace("ViewService >> updateInterface");

		LocalDate from = DateUtils.convert(textDateFrom.getText());
		LocalDate  to  = DateUtils.convert(textDateTo  .getText());

		Logger.debug("Selected date range: "+
				DateUtils.toString(from) +" - "+
				DateUtils.toString(to));
		
		Logger.debug("Selected categories: {"+
				StringUtils.toString(categories.getSelected())+
		"}");
		
		
		List<Transaction> filteredList = service
				.getCache(from,to);
		
		Set<String> categoriesInFilteredList =
				new HashSet<String>();

		Logger.debug("ViewService: Creating table...");
		
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
		
		Logger.debug("ViewService: Table created.");
		
		for(Button b: checkboxes)
				b.setEnabled(
						categoriesInFilteredList
						.contains(b.getText()));
		
		Logger.debug("ViewService: Window contents updated");
	}
	
	
	
	private void addEventListeners() {
		Logger.trace("ViewService >> addEventListeners");
		
		btnLoadNew.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Logger.trace("btnLoadNew: clicked");
				Logger.info("User clicked on 'btnLoadNew'");
				
				boolean theresNewData =
						service.loadNewTransactions();
				if(theresNewData) {
					textTotal.setText(service
							.getTotalBalance().toString());
					textDateFrom.setText(
							DateUtils.toString(service
							.getFirstDate()));
					textDateTo  .setText(
							DateUtils.toString(service
							.getLastDate()));
					
					for(String category: categories.getNew()) {
						categories.set(category, true);
						createCategoryCheckbox(category);
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
		Logger.debug("ViewService: SelectionListener added to 'calendarFrom'");
		
		
		calendarTo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {	// TODO find a better event
				Logger.trace("calendarTo: clicked");
				Logger.info("User selected from 'calendarTo'"+
							"and updated 'textDateTo'");
				closeCalendar(textDateTo, calendarTo);
			}
		});
		Logger.debug("ViewService: SelectionListener added to 'calendarTo'");
	}
	

	public void initCategories() {
		Logger.trace("ViewService >> initCategories");
		
		for(Transaction t: service.getCache())
			categories.set(t.getCategory(), true);

		for(String name : categories.get())
			createCategoryCheckbox(name);
	}
	
	
	private void createCategoryCheckbox(String name) {
		Logger.trace("ViewService >> createCategoryCheckbox");
		Button b = new Button(shell, SWT.CHECK);
		
		b.setLocation(categories.getNextButtonLocation());
		b.setSize(categories.getSize());
		
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
		
		checkboxes.add(b);
	}
	
	
	private void openCalendar(Text date, DateTime calendar) {
		Logger.trace("ViewService >> openCalendar");
		    date.setVisible(false);
		calendar.setVisible(true);
	}
	
	// TODO close calendar if clicked outside
	private void closeCalendar(Text date, DateTime calendar) {
		Logger.trace("ViewService >> closeCalendar");
		    date.setVisible(true);
		calendar.setVisible(false);
		
		date.setText(DateUtils.convert(calendar));
		updateInterface();
	}
}
