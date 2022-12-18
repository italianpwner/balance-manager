package model;

import java.math.BigDecimal;

public class Category {
	private String name;
	private BigDecimal amount;
	private Boolean selected;
	
	@SuppressWarnings("unused")
	private Category() {}
	
	public Category(String name, BigDecimal amount, Boolean selected) {
		this.name = name;
		this.amount = amount;
		this.selected = selected;
	}
	
	public void addAmount(BigDecimal amount) { this.amount = this.amount.add(amount); }
	
	public String getName() { return name; }
	public BigDecimal getAmount() { return amount; }
	public Boolean isSelected() { return selected; }
	public void setSelected(Boolean selected) { this.selected = selected; }
	public void setAmount(BigDecimal amount) { this.amount = amount; }
}
