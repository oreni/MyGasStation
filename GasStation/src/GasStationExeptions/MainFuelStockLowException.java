package GasStationExeptions;

@SuppressWarnings("serial")
public class MainFuelStockLowException extends Exception {
	public float amount;

	public MainFuelStockLowException(float amount) {
		super("Main Fuel Stock is Low left only " + amount + " Liters of Fuel");
		this.amount = amount;
	}
}
