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

	private CategoriesService categories;
	private List<Transaction> cache;
	private TransactionDAO dao;
	private int id;

	private static TransactionService instance;
	private TransactionService() {
		Logger.debug("TransactionService: creating cache...");
		
		categories = CategoriesService.getInstance();
		cache = new LinkedList<Transaction>();
		id = 1;

		dao = TransactionDAO.getInstance();
		updateCache(dao.getAll());
		
		Logger.debug("TransactionService: cache created.");
	}
	
	static TransactionService getInstance() {
		Logger.trace("TransactionService >> getInstance");
		
		if(instance == null)
			instance = new TransactionService();
		return instance;
	}
	
	BigDecimal getTotalBalance() {
		Logger.trace("TransactionService >> getTotalBalance");
		
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
		Logger.trace("TransactionService >> loadNewTransactions");
		Logger.debug("TransactionService: loading new transactions...");
		
		List<String> data = dao.writeToCSV();
		if(data.isEmpty())
			return false;
		else {
			updateCache(data);
			Logger.debug("TransactionService: new transactions loaded.");
		}
		return true;
	}
	
	private void updateCache(List<String> data) {
		Logger.trace("TransactionService >> updateCache");
		
		for(String s: data) {
			Transaction t = Transaction.make(s, id++);
			Logger.data(t.toString());
			cache.add(t);
			
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
	
	LocalDate getFirstDate() { return getDate(true) ; }
	LocalDate getLastDate () { return getDate(false); }
	private LocalDate getDate(boolean first) {
		if(cache.size() > 0) {
			String date = first
					? cache.get(0).getDate()
					: cache.get(cache.size()-1).getDate();
			return DateUtils.convert(date);
		}
		return LocalDate.now();
	}
	
}
