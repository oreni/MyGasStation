package GasStationController;

import java.io.Serializable;

public class DataFunction implements Serializable{

	private static final long serialVersionUID = 3L;

	private String functionName;
	private int licensePlate;
	
	public DataFunction(int carId, String functionName) {
		this.functionName = functionName;
		this.licensePlate = carId;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public int getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(int licensePlate) {
		this.licensePlate = licensePlate;
	}

}
