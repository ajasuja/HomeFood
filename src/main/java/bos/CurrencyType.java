package bos;

public enum CurrencyType {
	US_DOLLAR("$"),
	INDIAN_RUPEE("Rs.");
	
	private String symbol;
	
	private CurrencyType(String symbol) {
		this.symbol = symbol;
	}
	
	public String getSymbol(){
		return this.symbol;
	}
}

