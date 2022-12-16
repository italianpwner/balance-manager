package controller;

import java.awt.Toolkit;
import java.time.LocalDate;
import java.util.HashSet;
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
	private CategoriesService categories;

	private static ViewService instance;
	private ViewService() {

		instantiateWidgets();
		
		categories = CategoriesService.getInstance();
		canvasService = CanvasService.getInstance();
		transactionService = TransactionService.getInstance();
		
		setWidgetsSize();
		setWidgetsLocation();
		configureWidgets();

		initCategories();
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
		
		canvas = new Canvas(shell, SWT.BORDER);
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
		
		int tableWidth = 0;
		int[] column_width = {0, 35, 70, 75, 75, 350};
		for(int i=0; i<NUM_COLUMNS; i++) {
			int w = column_width[i];
			table.getColumn(i).setWidth(w);
			tableWidth += w;
		}
		table.setSize(
				tableWidth
				+ 25,
				shell.getSize().y
				- Padding.SMALL.get()*4);
		
		categories.setSize(70, 20);
		
		canvas.setSize(880, 500);
		canvasService.setHeight(canvas.getSize().y);
	}

	
	private void setWidgetsLocation() {
		Logger.trace("ViewService >> setWidgetsLocation");
		categories.setIncrement(100, 20);
		categories.perLine(4);
		
		table.setLocation(
				Padding.SMALL.get(),
				Padding.SMALL.get());
		
		lblTotal.setLocation(
				table.getLocation().x
				+ table.getSize().x
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
		
		categories.setStartingLocation(
				lblFrom.getLocation().x,
				textDateFrom.getLocation().y
				+ 2*textDateFrom.getSize().y
				+ Padding.BIG.get());
		
		categories.setNextButtonLocation();
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
		
		table.setHeaderVisible(true); // TODO autoscroll to bottom
		table.setLinesVisible(true);
		
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
			TableColumn col = table.getColumn(i);
			
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
				StringUtils.toString(categories.getSelected())+"}");
		
		
		List<Transaction> filteredList = transactionService
				.getCache(from,to);
		
		Set<String> categoriesInFilteredList =
				new HashSet<String>();

		Logger.debug("ViewService: Creating table...");
		
		table.setItemCount(0);
		if(redraw) canvasService.clear();
		
		for(Transaction t: filteredList) {
			
			// *****************   Update table   ****************
			if(categories.isSelected(t.getCategory()))
				newTableItem(t);
			
			// **************   Update checkboxes   **************
			categoriesInFilteredList.add(t.getCategory());

			// ****************   Update canvas   ****************
			if(redraw) canvasService.addSnapshot(t.getAmount());
		}
		if(redraw) canvas.redraw();
		
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
					
					for(String category: categories.getNew()) {
						categories.set(category, true);
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
				Logger.trace("canvas: painted");

				Button lastCheckbox = checkboxes
						.get(checkboxes.size()-1);
				canvas.setLocation(
						lblTotal.getLocation().x,
						lastCheckbox.getLocation().y
						+ lastCheckbox.getSize().y
						+ Padding.BIG.get());
				
				canvasService.setStep(canvas.getSize().x);
				canvasService.paintCanvas(e.gc, display);
			}
		});
		Logger.debug("ViewService: PaintListener added to 'canvas'");
	}
	

	public void initCategories() {
		Logger.trace("ViewService >> initCategories");
		
		for(Transaction t: transactionService.getCache())
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
	
	
	private void newTableItem(Transaction t) {
		TableItem item = new TableItem(table, SWT.NONE);
		
		item.setText(1, t.getId().toString());
		item.setText(2, t.getAmount());
		item.setText(3, t.getDate());
		item.setText(4, t.getCategory());
		item.setText(5, t.getDescription());
	}
}
