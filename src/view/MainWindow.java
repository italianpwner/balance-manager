package view;

import java.awt.Toolkit;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
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

	private static Logger logger =
			Logger.getInstance(Level.TRACE);
	
//*/*************   Widgets   **************/**/
/**/ protected static Shell shell;			/**/
/**/ protected static Table table;			/**/
/**/ protected static Text textTotBalance;	/**/
/**/ protected static Button btnLoadNew;	/**/
/**/ protected static Text textDateFrom;	/**/
/**/ protected static Text textDateTo;		/**/
//*/****************************************/**/

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String[] args) {
		logger.info("=== APPLICATION STARTED ===");
		logger.trace("MainWindow >> main");
		
		Properties appProps = AppProperties
				.getInstance().getProperties();
		String levelStr = appProps.getProperty("logger.level");
		Level level = Level.valueOf(levelStr);
		logger = Logger.getInstance(level);
		
		logger.info("Logger level set to "+level.get()+" ("+levelStr+")");
		
		try {
			MainWindow window =
					new MainWindow();
			window.open();
		} catch (Exception e) {
			logger.fatal("Failed to open window", e);
		}
	}

	/**
	 * Open the window.
	 */
	public void open() {
		logger.trace("MainWindow >> open");

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
		logger.trace("MainWindow >> createContents");

		
//*/***************   Shell   **************/**/
/**/	shell = new Shell();				/**/
/**/	shell.setText("Balance Manager");	/**/
/**/										/**/
/**/	java.awt.Dimension size = Toolkit	/**/
/**/			.getDefaultToolkit()		/**/
/**/			.getScreenSize();			/**/
/**/										/**/
/**/	shell.setSize(						/**/
/**/			(int)size.getWidth(),		/**/
/**/			(int)size.getHeight());		/**/
/**/	shell.setMaximized(true);			/**/
//*/****************************************/**/

		
//*/*********************   Balance   **********************/**/
/**/	Label lblTotBalance = new Label(shell, SWT.NONE);	/**/
/**/	lblTotBalance.setBounds(585, 16, 116, 20);			/**/
/**/	lblTotBalance.setText("Total balance (€):");		/**/
/**/														/**/
/**/	textTotBalance = new Text(shell,					/**/
/**/			SWT.BORDER | SWT.READ_ONLY | SWT.RIGHT);	/**/
/**/	textTotBalance.setEditable(false);					/**/
/**/	textTotBalance.setBounds(707, 13, 78, 26);			/**/
//*/********************************************************/**/


//*/******************   LoadButton   ******************/**/
/**/	btnLoadNew = new Button(shell, SWT.NONE);		/**/
/**/	btnLoadNew.setBounds(823, 10, 160, 30);			/**/
/**/	btnLoadNew.setText("Load new transactions");	/**/
//*/****************************************************/**/

		new ViewService();
	}
}
