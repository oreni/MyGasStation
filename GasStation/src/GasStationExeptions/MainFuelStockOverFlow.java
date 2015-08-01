package GasStationExeptions;

@SuppressWarnings("serial")
public class MainFuelStockOverFlow extends Exception {
	public float amount;

	public MainFuelStockOverFlow(float amout) {
		super("Main Fuel Stock OverFlow Added only " + amout + " Fuel");
		this.amount = amout;
	}
}
