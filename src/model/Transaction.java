package model;

import util.AppProperties;
import util.Logger;

public class Transaction {
	
	static Logger logger = Logger.getInstance();
	
	private int id;
	private String amount;
	private String date;
	private String category;
	private String description;
	
	@SuppressWarnings("unused")
	private Transaction() {}
	
	public Transaction(int id, String ... data)
	{
		this.id			= id;
		this.amount		= data[0];
		this.date		= data[1];
		this.category	= data[2];
		this.description= data[3];
	}

	public static Transaction make(String data, int id) {
		String[] fields = data.split(";");
		
		if(fields.length != 4) {
			String file = AppProperties
					.getInstance().getProperties()
					.getProperty("files.name.transactions");
			logger.error("Invalid data '"+data+"' at line "+id+" of '"+file+
					"' (expected <amount>;<date>;<category>;<description>)");
		}
		return new Transaction(
				id, fields[0], fields[1],
					fields[2], fields[3]);
	}

	@Override
	public String toString() {
		return String.format(
				"{id: %03d, amount: %10s, date: %s, category: %-12s, description: '%s'}",
				id, amount, date, "'"+category+"'", description
		);
	}
	
	public Integer getId		 () { return id			; }
	public String  getAmount	 () { return amount		; }
	public String  getDate	   	 () { return date		; }
	public String  getCategory   () { return category	; }
	public String  getDescription() { return description; }
}
