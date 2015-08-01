package GasStationBL;

public class Statistics {
	private int mainFuelStockRefuelingTimes;
	private float TotalMainFuelRefuelTime;
	private float amountMainFueled;

	private int pumpFuelingTimes;
	private float TotalPumpFuelngTime;
	private float amountFueled;

	private int washTimes;
	private float totalWashTime;

	private GasStation context;

	public Statistics(GasStation context) {
		this.mainFuelStockRefuelingTimes = 0;
		this.amountMainFueled = 0;
		this.TotalMainFuelRefuelTime = 0;
		this.pumpFuelingTimes = 0;
		this.amountFueled = 0;
		this.TotalPumpFuelngTime = 0;
		this.washTimes = 0;
		this.totalWashTime = 0;
		this.context = context;
	}

	public void addFuelingInfo(float time, float amount) {
		this.pumpFuelingTimes++;
		this.amountFueled += amount;
	}

	public void addMainFuelingInfo(float time, float amount) {
		this.mainFuelStockRefuelingTimes++;
		this.amountMainFueled += amount;
	}

	public void addWashingInfo(float time) {
		this.washTimes++;
	}

	public int getMainFuelStockRefuelingTimes() {
		return mainFuelStockRefuelingTimes;
	}

	public float getTotalMainFuelRefuelTime() {
		return TotalMainFuelRefuelTime;
	}

	public float getAmountMainFueled() {
		return amountMainFueled;
	}

	public int getPumpFuelingTimes() {
		return pumpFuelingTimes;
	}

	public float getTotalPumpFuelngTime() {
		return TotalPumpFuelngTime;
	}

	public float getAmountFueled() {
		return amountFueled;
	}

	public int getWashTimes() {
		return washTimes;
	}

	public float getTotalWashTime() {
		return totalWashTime;
	}

	public String toString() {
		String stats = "";
		stats += "\n********Fueling INFO************";
		stats += "\nFueling Times: " + getPumpFuelingTimes();
		stats += "\nFueling Total Time: " + getTotalPumpFuelngTime();
		stats += "\nTotal Fuel Pumped: " + getAmountFueled();
		stats += "\nFueling profits: "
				+ String.format("%.02f",
						getPumpFuelingTimes() * context.getPricePerLiter());
		stats += "\n********Main Fuel INFO**********";
		stats += "\nFueling Times: " + getMainFuelStockRefuelingTimes();
		stats += "\nFueling Total Time: " + getTotalMainFuelRefuelTime();
		stats += "\nTotal Fuel Added: " + getAmountMainFueled();
		stats += "\n********Washinging INFO***********";
		stats += "\nWashing Times: " + getWashTimes();
		stats += "\nWash Total Time: " + getTotalWashTime();
		stats += "\nWashing profits: "
				+ String.format("%.02f", getWashTimes()
						* context.getCleaningService().getPrice());
		return stats;
	}

}
