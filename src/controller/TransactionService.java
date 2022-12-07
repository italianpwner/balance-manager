package controller;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import model.Transaction;
import persistence.TransactionDAO;
import util.Logger;

public class TransactionService {
	
	static Logger logger = Logger.getInstance();
	
	private List<Transaction> cache;
	private Set<String> categories;
	private TransactionDAO dao;
	private int id;
	
	private static TransactionService instance;
	private TransactionService() {
		logger.info("TransactionService: creating cache...");
		
		cache = new LinkedList<Transaction>();
		categories = new HashSet<String>();
		id = 0;

		dao = TransactionDAO.getInstance();
		updateCache(dao.getAll());
		
		logger.info("TransactionService: cache created.");
	}
	
	public static TransactionService getInstance() {
		logger.trace("TransactionService >> getInstance");
		
		if(instance == null)
			instance = new TransactionService();
		return instance;
	}
	
	public Double getTotalBalance() {
		logger.trace("TransactionService >> getTotalBalance");
		
		// TODO fix floating point errors
		Double balance = 0.0;
		for(Transaction t: cache)
			balance += t.getAmount();
		return balance;
	}
	
	public boolean loadNewTransactions() {
		logger.trace("TransactionService >> loadNewTransactions");
		logger.info("TransactionService: loading new transactions...");
		
		List<String> data = dao.writeToCSV();
		if(data.isEmpty())
			return false;
		else {
			updateCache(data);
			logger.info("TransactionService: new transactions loaded.");
		}
		return true;
	}
	
	private void updateCache(List<String> data) {
		logger.trace("TransactionService >> updateCache");
		
		for(String s: data) {
			Transaction t = Transaction.make(s, id++);
			logger.data(t.toString());
			
			categories.add(t.getCategory());
			cache.add(t);
		}
	}

	public List<Transaction> getCache(
			Set<String> selectedCategories,
			LocalDate from, LocalDate to)
	{
		List<Transaction> list =
				new LinkedList<Transaction>();
		for(Transaction t: cache)
			if(selectedCategories.contains(t.getCategory())
				&& t.getDate().isAfter(from.minusDays(1))
				&& t.getDate().isBefore(to.minusDays(1))
			) {
				list.add(t);
			}
		return list;
	}
	
	public Set<String> getCategories() { return categories; }
	
	public LocalDate getFirstDate() {
		return cache.get(0).getDate();
	}
	
	public LocalDate getLastDate () {
		return cache.get(cache.size()-1).getDate();
	}
}
