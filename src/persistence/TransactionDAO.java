package persistence;

import java.util.List;

import util.FileUtils;
import util.Logger;
import util.Properties;

public class TransactionDAO {
	
	static Logger logger = Logger.getInstance();
	
	private static TransactionDAO instance;
	private static String fileName_newTransactions;
	private static String fileName_transactions;
	
	private TransactionDAO() {};
	public static TransactionDAO getInstance() {
		logger.trace("TransactionDAO >> getInstance");
		if(instance == null) {
			instance = new TransactionDAO();
			
			java.util.Properties appProps =
					Properties.getInstance().getProperties();
			
			String filePath = appProps.getProperty("files.path");
			fileName_newTransactions =
					 filePath + appProps.getProperty("files.name.new_transactions");
			fileName_transactions =
					 filePath + appProps.getProperty("files.name.transactions");
		}
		return instance;
	}
	
	public List<String> getAll() {
		logger.trace("TransactionDAO >> getAll");
		return FileUtils.read(fileName_transactions);
	}

	public List<String> writeToCSV() {
		List<String> data =
				FileUtils.read(fileName_newTransactions);
		
		if(data.isEmpty())
			logger.info("TransactionDAO: no new transactions.");
		else {
			logger.trace("TransactionDAO >> writeToCSV");
			
			FileUtils.write(fileName_transactions, data);
			FileUtils.deleteContents(fileName_newTransactions);
		}
		
		return data;
	}
}
