package controller;

import java.awt.Toolkit;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
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
		
	private TransactionService transactionService;
	private CanvasService canvasService;
	private CategoriesService categoriesService;

	private static ViewService instance;
	private ViewService() {

		instantiateWidgets();
		
		categoriesService = CategoriesService.getInstance();
		canvasService = CanvasService.getInstance();
		transactionService = TransactionService.getInstance();
		
		setWidgetsSize();
		setWidgetsLocation();
		configureWidgets();

		initCategories();
		setCanvasLocation();
		addEventListeners();
		updateInterface(true);
	}
	
	public static ViewService getInstance() {
		Logger.trace("ViewService >> getInstance");
		
		if(instance == null)
			instance = new ViewService();
		return instance;
	}
	
	
	private void instantiateWidgets() {
		Logger.trace("ViewService >> instantiateWidgets");

		shell = new Shell(); // TODO review SWT styles
		checkboxes = new LinkedList<Button>();
		
		btnLoadNew = new Button(shell, SWT.NONE);
		lblTotal   = new Label (shell, SWT.NONE);
		lblFrom    = new Label (shell, SWT.NONE);
		lblTo      = new Label (shell, SWT.NONE);

		textTotal	 = new Text(shell, SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);
		textDateFrom = new Text(shell, SWT.BORDER);
		textDateTo   = new Text(shell, SWT.BORDER);
		
		calendarFrom = new DateTime(shell, SWT.CALENDAR);
		calendarTo   = new DateTime(shell, SWT.CALENDAR);

		canvas = new Canvas(shell, SWT.BORDER);
		
		categoriesTable   = new Table(shell, SWT.BORDER | SWT.NO_SCROLL);
		transactionsTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		
		for(int i=0; i<NUM_COLUMNS; i++)
			new TableColumn(transactionsTable, SWT.NONE);
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
		
		int default_h = 20;
		
		btnLoadNew.setSize(140,  30);
		textTotal .setSize( 78,  default_h);

		lblTotal.setSize(90, default_h);
		lblFrom	.setSize(30, default_h);
		lblTo	.setSize(15, default_h);

		p.x = 70; p.y = default_h;
		textDateFrom.setSize(p);
		textDateTo	.setSize(p);

		p.x = 225; p.y = 145;
		calendarFrom.setSize(p);
		calendarTo	.setSize(p);
		
		int tableWidth = 0;
		int[] column_width = {0, 35, 70, 75, 75, 350};
		for(int i=0; i<NUM_COLUMNS; i++) {
			int w = column_width[i];
			transactionsTable.getColumn(i).setWidth(w);
			tableWidth += w;
		}
		transactionsTable.setSize(
				tableWidth
				+ 21, // Scrollbar width
				shell.getSize().y
				- Padding.SMALL.get()*4); // Window title bar height
		
		int categories_w = 70;
		categoriesService.setSize(categories_w, default_h);
		categoriesTable.setSize(categories_w, 50);
		
		canvas.setSize(880, 500);
		canvasService.setHeight(canvas.getSize().y);
	}

	
	private void setWidgetsLocation() {
		Logger.trace("ViewService >> setWidgetsLocation");
		categoriesService.setIncrement(100, 20);
		categoriesService.perLine(4);
		
		transactionsTable.setLocation(
				Padding.SMALL.get(),
				Padding.SMALL.get());
		
		lblTotal.setLocation(
				transactionsTable.getLocation().x
				+ transactionsTable.getSize().x
				+ Padding.BIG.get(),
				Padding.SMALL.get()
				+ (btnLoadNew.getSize().y
					- lblTotal.getSize().y) / 2
				+ lblTotal.getSize().y / 8);
		
		textTotal.setLocation(
				lblTotal.getLocation().x
				+ lblTotal.getSize().x
				+ Padding.BIG.get(),
				Padding.SMALL.get()
				+ (btnLoadNew.getSize().y
					- lblTotal.getSize().y) / 2);
		
		btnLoadNew.setLocation(
				textTotal.getLocation().x
				+ textTotal.getSize().x
				+ Padding.BIG.get(),
				Padding.SMALL.get());
		
		lblFrom.setLocation(
				lblTotal.getLocation().x,
				lblTotal.getLocation().y
				+ lblTotal.getSize().y
				+ Padding.BIG.get()
				+ lblFrom.getSize().y / 8);
		
		textDateFrom.setLocation(
				lblFrom.getLocation().x
				+ lblFrom.getSize().x
				+ Padding.SMALL.get(),
				lblFrom.getLocation().y
				- lblFrom.getSize().y / 8);
		
		calendarFrom.setLocation(
				textDateFrom.getLocation().x,
				textDateFrom.getLocation().y);

		lblTo.setLocation(
				calendarFrom.getLocation().x
				+ calendarFrom.getSize().x
				+ Padding.SMALL.get(),
				lblFrom.getLocation().y);
		
		textDateTo.setLocation(
				lblTo.getLocation().x
				+ lblTo.getSize().x
				+ Padding.SMALL.get(),
				textDateFrom.getLocation().y);
		
		calendarTo.setLocation(
				textDateTo.getLocation().x,
				textDateTo.getLocation().y);
		
		categoriesService.setStartingLocation(
				lblFrom.getLocation().x,
				textDateFrom.getLocation().y
				+ 2*textDateFrom.getSize().y
				+ Padding.BIG.get());
		
		categoriesService.setNextButtonLocation();
		// TODO print widgets location to check
	}
	
	
	private void configureWidgets() {
		Logger.trace("ViewService >> configureWidgets");
		
		shell.setText("Balance Manager");
		shell.setMaximized(true);
		
		btnLoadNew.setText("Load new transactions");
		lblTotal.setText("Total balance (€):");
		lblFrom.setText("From");
		lblTo.setText("To");
		
		transactionsTable.setHeaderVisible(true); // TODO autoscroll to bottom
		transactionsTable.setLinesVisible(true);
		
		categoriesTable.setHeaderVisible(true);
		categoriesTable.setLinesVisible(true);
		
		String    balance   = transactionService.getTotalBalance().toString();
		LocalDate firstDate = transactionService.getFirstDate();
		LocalDate lastDate  = transactionService.getLastDate();
		
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
			TableColumn col = transactionsTable.getColumn(i);
			
			col.setResizable(false);
			col.setText(column_text[i]);
		}
	}
	
	
	private void updateInterface(boolean redraw) {
		Logger.trace("ViewService >> updateInterface");

		LocalDate from = DateUtils.convert(textDateFrom.getText());
		LocalDate  to  = DateUtils.convert(textDateTo  .getText());

		Logger.debug("Selected date range: "+
				DateUtils.toString(from) +" - "+
				DateUtils.toString(to));
		
		Logger.debug("Selected categories: {"+
				StringUtils.toString(categoriesService.getSelected())+"}");
		
		
		List<Transaction> filteredList = transactionService
				.getCache(from,to);
		
		Set<String> categoriesInFilteredList =
				new HashSet<String>();
		
		transactionsTable.setItemCount(0);
		if(redraw) {
			canvasService.clear();
			categoriesService.resetAmounts(); // TODO should update if different date range
		}
		
		for(Transaction t: filteredList) {
			String category = t.getCategory();
			BigDecimal amount = new BigDecimal(t.getAmount());
			
			// *****************   Update transactions table   ****************
			if(categoriesService.isSelected(t.getCategory())) {
				TableItem item = new TableItem(
						transactionsTable, SWT.NONE);
				
				item.setText(1, t.getId().toString());
				item.setText(2, t.getAmount());
				item.setText(3, t.getDate());
				item.setText(4, category);
				item.setText(5, t.getDescription());
			}
			
			// *****************   Update categories table   ****************
			categoriesService.updateCategories(category, amount);
			
			// **************   Update checkboxes   **************
			categoriesInFilteredList.add(category);

			// ****************   Update canvas   ****************
			if(redraw) canvasService.addSnapshot(amount);
		}
		if(redraw) {
			canvas.redraw();
			setCategoriesTableLocation();
			
			TableItem item = new TableItem(
					categoriesTable, SWT.NONE);
			
			Set<String> set = categoriesService.get();
			List<String> categories = new ArrayList<String>(set);
			for(int i=0; i<categories.size(); i++)
				item.setText(i, categoriesService
						.getAmount(categories.get(i)));
		}
		
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
						transactionService.loadNewTransactions();
				if(theresNewData) {
					textTotal.setText(transactionService
							.getTotalBalance().toString());
					textDateFrom.setText(
							DateUtils.toString(transactionService
							.getFirstDate()));
					textDateTo  .setText(
							DateUtils.toString(transactionService
							.getLastDate()));
					
					for(String category: categoriesService.getNew()) {
						categoriesService.updateCategories(category, true);
						createCategoryCheckbox(category);
					}
										
					updateInterface(true);
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
		
		
		canvas.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				Logger.trace("canvas: paint");
				
				setCanvasLocation();
				canvasService.setStep(canvas.getSize().x);
				canvasService.paintCanvas(e.gc, display);
			}
		});
		Logger.debug("ViewService: PaintListener added to 'canvas'");
	}
	

	private void initCategories() {
		Logger.trace("ViewService >> initCategories");
		
		for(Transaction t: transactionService.getCache())
			categoriesService.updateCategories(t.getCategory(), true);

		int colWidth = categoriesTable.getSize().x;
		Set<String> categories = categoriesService.get();
		categoriesTable.setSize(
				colWidth*categories.size(),
				categoriesTable.getSize().y);
		
		for(String name: categories) {
			createCategoryCheckbox(name);

			TableColumn col = new TableColumn(
					categoriesTable, SWT.NONE);
			col.setResizable(false);
			col.setText(name);
			col.setWidth(colWidth);
		}
	}
	
	
	private void createCategoryCheckbox(String name) {
		Button b = new Button(shell, SWT.CHECK);
		
		b.setLocation(categoriesService.getNextButtonLocation());
		b.setSize(categoriesService.getSize());
		
		b.setText(name);
		b.setSelection(categoriesService.isSelected(name));
		
		b.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean selected = b.getSelection();
				
				String action = (selected?"":"un")+"checked";
				Logger.trace("btnCheckbox"+name+": "+action);
				Logger.info("User "+action+" CheckBox '"+name+"'");
				
				categoriesService.updateCategories(name,selected);
				updateInterface(false);
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
		updateInterface(true);
	}
	
	
	private void setCanvasLocation() {
		Logger.trace("ViewService >> setCanvasLocation");
		
		Button lastCheckbox = checkboxes
				.get(checkboxes.size()-1);
		canvas.setLocation(
				lblTotal.getLocation().x,
				lastCheckbox.getLocation().y
				+ lastCheckbox.getSize().y
				+ Padding.BIG.get());
	}
	
	private void setCategoriesTableLocation() {
		Logger.trace("ViewService >> setCategoriesTableLocation");
		
		categoriesTable.setLocation(
				canvas.getLocation().x,
				canvas.getLocation().y
				+ canvas.getSize().y
				+ Padding.BIG.get());
	}
}
