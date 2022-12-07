package controller;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableItem;

import model.Transaction;
import util.DateUtils;
import util.Logger;

public class ViewService extends view.MainWindow {
	
	static Logger logger = Logger.getInstance();
	static TransactionService service = TransactionService.getInstance();

	public static void updateInterface() {
		logger.trace("ViewService >> updateInterface");

		List<Transaction> list = service.getCache();
		
		table.setItemCount(0);
		for(int i=0; i<list.size(); i++) {
			TableItem item = new TableItem(table, SWT.NONE);
			Transaction t = list.get(i);
			
			item.setText(1, t.getId().toString());
			item.setText(2, t.getAmount().toString());
			item.setText(3, DateUtils.toString(t.getDate()));
			item.setText(4, t.getCategory());
			item.setText(5, t.getDescription());
		}
	}
}