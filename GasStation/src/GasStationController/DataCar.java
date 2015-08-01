package GasStationController;

import java.io.Serializable;

public class DataCar implements Serializable {

	private static final long serialVersionUID = 2L;
	
	int licensePlate;
	boolean wantsWashing;
	boolean wantsFuel;	
	float fuelWanted;
	
	public DataCar(int licensePlate,boolean wantsWashing,boolean wantsFuel,float fuelWanted) {
		this.licensePlate = licensePlate;	
		this.wantsWashing = wantsWashing;	
		this.wantsFuel = wantsFuel;
		this.fuelWanted = fuelWanted;
	}

	public int getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(int licensePlate) {
		this.licensePlate = licensePlate;
	}

	public boolean isWantsWashing() {
		return wantsWashing;
	}

	public void setWantsWashing(boolean wantsWashing) {
		this.wantsWashing = wantsWashing;
	}

	public boolean isWantsFuel() {
		return wantsFuel;
	}

	public void setWantsFuel(boolean wantsFuel) {
		this.wantsFuel = wantsFuel;
	}

	public float getFuelWanted() {
		return fuelWanted;
	}

	public void setFuelWanted(float fuelWanted) {
		this.fuelWanted = fuelWanted;
	}
}
