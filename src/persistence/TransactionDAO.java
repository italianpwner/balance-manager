package persistence;

import java.util.List;
import java.util.Properties;

import util.AppProperties;
import util.FileUtils;
import util.Logger;

public class TransactionDAO {

	private static TransactionDAO instance;
	private static String fileName_newTransactions;
	private static String fileName_transactions;
	
	private TransactionDAO() {};
	public static TransactionDAO getInstance() {
		Logger.trace("TransactionDAO >> getInstance");
		if(instance == null) {
			instance = new TransactionDAO();
			
			Properties appProps = AppProperties
					.getInstance().getProperties();
			
			String filePath = appProps.getProperty("files.path");
			fileName_newTransactions =
					 filePath + appProps.getProperty("files.name.new_transactions");
			fileName_transactions =
					 filePath + appProps.getProperty("files.name.transactions");
		}
		return instance;
	}
	
	public List<String> getAll() {
		Logger.trace("TransactionDAO >> getAll");
		return FileUtils.read(fileName_transactions);
	}

	public List<String> writeToCSV() {
		List<String> data =
				FileUtils.read(fileName_newTransactions);
		
		if(data.isEmpty())
			Logger.info("TransactionDAO: no new transactions.");
		else {
			Logger.trace("TransactionDAO >> writeToCSV");
			
			FileUtils.write(fileName_transactions, data);
			FileUtils.deleteContents(fileName_newTransactions);
		}
		
		return data;
	}
}
