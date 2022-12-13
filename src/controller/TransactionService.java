package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import model.Transaction;
import persistence.TransactionDAO;
import util.DateUtils;
import util.Logger;

public class TransactionService {
	
	static Logger logger = Logger.getInstance();

	private List<Transaction> cache;
	private TransactionDAO dao;
	private int id;
	
	private static TransactionService instance;
	private TransactionService() {
		logger.debug("TransactionService: creating cache...");
		
		cache = new LinkedList<Transaction>();
		id = 1;

		dao = TransactionDAO.getInstance();
		_updateCache(dao.getAll());
		
		logger.debug("TransactionService: cache created.");
	}
	
	static TransactionService getInstance() {
		logger.trace("TransactionService >> getInstance");
		
		if(instance == null)
			instance = new TransactionService();
		return instance;
	}
	
	BigDecimal getTotalBalance() {
		logger.trace("TransactionService >> getTotalBalance");
		
		BigDecimal balance =
				new BigDecimal("0.0");
		for(Transaction t: cache) {
			BigDecimal tAmount =
					new BigDecimal(t.getAmount());
			balance = balance.add(tAmount);
		}
		return balance;
	}
	
	boolean loadNewTransactions() {
		logger.trace("TransactionService >> loadNewTransactions");
		logger.debug("TransactionService: loading new transactions...");
		
		List<String> data = dao.writeToCSV();
		if(data.isEmpty())
			return false;
		else {
			_updateCache(data);
			logger.debug("TransactionService: new transactions loaded.");
		}
		return true;
	}
	
	private void _updateCache(List<String> data) {
		logger.trace("TransactionService >> _updateCache");
		
		for(String s: data) {
			Transaction t = Transaction.make(s, id++);
			logger.data(t.toString());
			cache.add(t);
			
			CategoriesService categories =
					CategoriesService.getInstance();
			String category = t.getCategory();
			
			if(categories.isNew(category))
				categories.set(category, null);
		}
	}

	List<Transaction> getCache() { return cache; }
	List<Transaction> getCache(LocalDate from, LocalDate to) {
		List<Transaction> filteredList =
				new LinkedList<Transaction>();
		
		for(Transaction t: cache) {
			LocalDate tDate = DateUtils.convert(t.getDate());
		
			if(tDate.isAfter(from.minusDays(1))
				&& tDate.isBefore(to.plusDays(1))
			) {
				filteredList.add(t);
			}
		}
		return filteredList;
	}
	
	LocalDate getFirstDate() { return _getDate(true) ; }
	LocalDate getLastDate () { return _getDate(false); }
	private LocalDate _getDate(boolean first) {
		if(cache.size() > 0) {
			String date = first
					? cache.get(0).getDate()
					: cache.get(cache.size()-1).getDate();
			return DateUtils.convert(date);
		}
		return LocalDate.now();
	}
	
}
