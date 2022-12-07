package model;

import java.time.LocalDate;

import util.DateUtils;
import util.Logger;

public class Transaction {
	
	static Logger logger = Logger.getInstance();
	
	private Integer id;
	private Double amount;
	private LocalDate date;
	private String category;
	private String description;
	
	@SuppressWarnings("unused")
	private Transaction() {}
	
	public Transaction(Integer id, Double amount, LocalDate date,
			String category, String description)
	{
		this.id			= id;
		this.amount		= amount;
		this.date		= date;
		this.category	= category;
		this.description= description;
	}

	public static Transaction make(String data, int id) {
		String[] fields = data.split(";");
		return new Transaction(
				id,
				Double.parseDouble(fields[0]),
				DateUtils.convert(fields[1]),
				fields[2],
				fields[3]
		);
	}

	@Override
	public String toString() {
		return String.format(
				"{id: %03d, amount: %8.2f, date %s, category: %-12s, description: '%s'}",
				id, amount, DateUtils.toString(date), "'"+category+"'", description
		);
	}
	
	public Integer	 getId		   () { return id;			}
	public Double	 getAmount	   () { return amount;		}
	public LocalDate getDate	   () { return date;		}
	public String	 getCategory   () { return category;	}
	public String	 getDescription() { return description; }
}
