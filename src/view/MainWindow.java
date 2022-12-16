package view;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import controller.ViewService;
import util.AppProperties;
import util.Level;
import util.Logger;

public class MainWindow {
	
	@SuppressWarnings("unused")
	private static ViewService service;
	
	protected static Shell   shell;
	protected static Display display;
	
	protected static final int NUM_COLUMNS = 6;
	protected static Table table;
	
	protected static Label lblTotal;
	protected static Text  textTotal;

	protected static Label lblFrom;
	protected static Text  textDateFrom;

	protected static Label lblTo;
	protected static Text  textDateTo;
	
	protected static DateTime calendarFrom;
	protected static DateTime calendarTo;

	protected static Button btnLoadNew;
	protected static List<Button> checkboxes;
	
	protected static Canvas canvas;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.set(Level.INFO);
		Logger.info("=== APPLICATION STARTED ===");
		
		initLogger();
		checkboxes = new LinkedList<Button>();
		
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			Logger.fatal("Failed to open window", e);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Logger.trace("MainWindow >> open");

		display = Display.getDefault();
		createContents();
		shell.open();
		shell.layout();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		Logger.trace("MainWindow >> createContents");
		service = ViewService.getInstance();
	}
	
	private static void initLogger() {
		Logger.trace("MainWindow >> initLogger");
		Properties appProps = AppProperties
				.getInstance().getProperties();
		String levelStr = appProps.getProperty("logger.level");
		Level level = Level.valueOf(levelStr);
		Logger.set(level);
		
		Logger.info("Logger level set to "+level.get()+" ("+levelStr+")");
	}
}
