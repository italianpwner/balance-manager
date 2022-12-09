package controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.Transaction;
import persistence.TransactionDAO;
import util.DateUtils;
import util.Logger;

public class TransactionService {
	
	static Logger logger = Logger.getInstance();
	
	private List<Transaction> cache;
	private Set<String> categories;
	private TransactionDAO dao;
	private int id;
	
	private static TransactionService instance;
	private TransactionService() {
		logger.debug("TransactionService: creating cache...");
		
		cache = new LinkedList<Transaction>();
		categories = new HashSet<String>();
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
			
			categories.add(t.getCategory());
			cache.add(t);
		}
	}

	List<Transaction> getCache(
			Set<String> selectedCategories,
			LocalDate from, LocalDate to)
	{
		List<Transaction> list =
				new LinkedList<Transaction>();
		
		for(Transaction t: cache) {
			LocalDate tDate = DateUtils.convert(t.getDate());
		
			if(selectedCategories.contains(t.getCategory())
				&& tDate.isAfter(from.minusDays(1))
				&& tDate.isBefore(to.plusDays(1))
			) {
				list.add(t);
			}
		}
		return list;
	}
	
	private LocalDate _getDate(boolean first) {
		if(cache.size() > 0) {
			String date = first
					? cache.get(0).getDate()
					: cache.get(cache.size()-1).getDate();
			return DateUtils.convert(date);
		}
		return LocalDate.now();
	}
	
	LocalDate getFirstDate() { return _getDate(true) ; }
	LocalDate getLastDate () { return _getDate(false); }
	Set<String> getCategories() { return categories; }
}
