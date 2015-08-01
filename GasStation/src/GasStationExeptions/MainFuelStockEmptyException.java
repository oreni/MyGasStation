package GasStationExeptions;

@SuppressWarnings("serial")
public class MainFuelStockEmptyException extends Exception {
	public float amount;

	public MainFuelStockEmptyException(float amount) {
		super("Main Fuel Stock is Empty Fueled only:" + amount + " Liters");
		this.amount = amount;
	}
}
