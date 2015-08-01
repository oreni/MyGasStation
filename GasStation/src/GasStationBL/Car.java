package GasStationBL;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import GasStationLogging.*;

public class Car {
	private static Logger logger = Logger.getLogger("GSLog");
	private int licensePlate;
	private boolean wantWashing;
	private boolean wantFuel;
	private float numOfLitersWanted;
	private int wantFuelPumpNum;
	private Handler logHandler;

	public Car(int licensePlate, boolean wantWashing) throws SecurityException,
			IOException {
		this.licensePlate = licensePlate;
		this.wantWashing = wantWashing;
		this.numOfLitersWanted = 0;
		this.wantFuelPumpNum = 0;
		this.wantFuel = false;
		logHandler = new FileHandler("logs/Cars/Car-" + licensePlate + ".xml");
		logHandler.setFilter(new GasStationFilter(this));
		logHandler.setFormatter(new GasStationFormat());
		logger.addHandler(logHandler);
	}

	public int getLicensePlate() {
		return licensePlate;
	}

	public boolean isWantWashing() {
		return wantWashing;
	}

	public boolean isWantFuel() {
		return wantFuel;
	}

	public void setFuelPumpWanted(int wantFuelPumpNum) {
		this.wantFuelPumpNum = wantFuelPumpNum;
	}

	public int getFuelPumpNumWanted() {
		return wantFuelPumpNum;
	}

	public void setWantWashing(boolean wantCleaning) {
		this.wantWashing = wantCleaning;
	}

	public void setWantFuel(boolean wantFuel) {
		this.wantFuel = wantFuel;
	}

	public void setWantFuelNumOfLiters(float wantFuelNumOfLiters) {
		this.numOfLitersWanted = wantFuelNumOfLiters;
	}

	public float getWantFuelNumOfLiters() {
		return numOfLitersWanted;
	}

	public void dispose() throws SecurityException {
		logHandler.close();
	}

	@WhileCleaning
	public void readPaper() {
		System.out.println("readPaper");
	}

	@WhileCleaning
	public void playOnPhone() {
		System.out.println("playOnPhone");
	}

	@WhileCleaning
	public void talkOnPhone() {
		System.out.println("talkOnPhone");
	}
}
