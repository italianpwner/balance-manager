package view;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import util.Level;
import util.Logger;
import util.Properties;

public class MainWindow {

	static Logger logger = Logger.getInstance(Level.ALL, true, true);
	
	protected Shell shell;

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("=== APPLICATION STARTED ===");
		logger.trace("Index >> main");
		
		java.util.Properties appProps =
				Properties.getInstance().getProperties();
		
		logger = Logger.getInstance(
				Level.valueOf(appProps.getProperty("logger.level")),
				Boolean.parseBoolean(appProps.getProperty("logger.trace")),
				Boolean.parseBoolean(appProps.getProperty("logger.data"))
		);
		
		try {
			MainWindow window = new MainWindow();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		Display display = Display.getDefault();
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
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("SWT Application");

	}

}
