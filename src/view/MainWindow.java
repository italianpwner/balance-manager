package view;

import java.awt.Toolkit;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;

import controller.ViewService;
import util.AppProperties;
import util.Level;
import util.Logger;

public class MainWindow {

	private static Logger logger =
			Logger.getInstance(Level.ALL, true, true);
	
//*/*************   Widgets   **************/**/
/**/ protected static Shell shell;			/**/
/**/ protected static Table table;			/**/
/**/ protected static Text textTotBalance;	/**/
/**/ protected static Button btnLoadNew;	/**/
/**/ protected static DateTime dateTimeFrom;/**/
/**/ protected static DateTime dateTimeTo;	/**/
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
		
		logger = Logger.getInstance(
				Level.valueOf(appProps.getProperty("logger.level")),
				Boolean.parseBoolean(appProps.getProperty("logger.trace")),
				Boolean.parseBoolean(appProps.getProperty("logger.data"))
		);
		try {
			MainWindow window =
					new MainWindow();
			window.open();
		} catch (Exception e) {
			logger.fatal(e);
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


//*********************************   Table   *********************************/
/**/	logger.info("MainWindow: Creating table...");						/**/
/**/																		/**/
/**/	table = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);			/**/
/**/	table.setBounds(10, 10, 555, 630);									/**/
/**/	table.setHeaderVisible(true);										/**/
/**/	table.setLinesVisible(true);										/**/
/**/																		/**/
/**/	TableColumn tblclmn_0 = new TableColumn(table, SWT.NONE);			/**/
/**/	tblclmn_0.setResizable(false);										/**/
/**/	tblclmn_0.setWidth(0);												/**/
/**/																		/**/
/**/	TableColumn tblclmn_id = new TableColumn(table, SWT.CENTER);		/**/
/**/	tblclmn_id.setResizable(false);										/**/
/**/	tblclmn_id.setWidth(30);											/**/
/**/	tblclmn_id.setText("Id");											/**/
/**/																		/**/
/**/	TableColumn tblclmn_amount = new TableColumn(table, SWT.RIGHT);		/**/
/**/	tblclmn_amount.setResizable(false);									/**/
/**/	tblclmn_amount.setWidth(80);										/**/
/**/	tblclmn_amount.setText("Amount");									/**/
/**/																		/**/
/**/	TableColumn tblclmn_date = new TableColumn(table, SWT.CENTER);		/**/
/**/	tblclmn_date.setResizable(false);									/**/
/**/	tblclmn_date.setWidth(90);											/**/
/**/	tblclmn_date.setText("Date");										/**/
/**/																		/**/
/**/	TableColumn tblclmn_category = new TableColumn(table, SWT.LEFT);	/**/
/**/	tblclmn_category.setResizable(false);								/**/
/**/	tblclmn_category.setWidth(90);										/**/
/**/	tblclmn_category.setText("Category");								/**/
/**/																		/**/
/**/	TableColumn tblclmn_description = new TableColumn(table, SWT.LEFT);	/**/
/**/	tblclmn_description.setResizable(false);							/**/
/**/	tblclmn_description.setWidth(260); 									/**/
/**/	tblclmn_description.setText("Description");							/**/
/**/																		/**/
/**/	logger.info("MainWindow: Table created.");							/**/
//*****************************************************************************/


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


//*/*************************   DateTimes   ************************/**/
/**/	Label lblDateTimeFrom = new Label(shell, SWT.NONE);			/**/
/**/	lblDateTimeFrom.setBounds(585, 150, 40, 20);				/**/
/**/	lblDateTimeFrom.setText("From");							/**/
/**/																/**/
/**/	dateTimeFrom = new DateTime(shell, SWT.BORDER);				/**/
/**/	dateTimeFrom.setBounds(631, 145, 102, 28);					/**/
/**/																/**/
/**/																/**/
/**/	Label lblDateTimeTo = new Label(shell, SWT.NONE);			/**/
/**/	lblDateTimeTo.setBounds(778, 150, 22, 20);					/**/
/**/	lblDateTimeTo.setText("To");								/**/
/**/																/**/
/**/	dateTimeTo = new DateTime(shell, SWT.BORDER);				/**/
/**/	dateTimeTo.setBounds(806, 145, 102, 28);					/**/
//*/****************************************************************/**/

		ViewService.initBalance();
		ViewService.initDateTimes();
		ViewService.addEventListeners();
		ViewService.updateInterface();
		
		logger.info("MainWindow: window contents created.");
	}
}
