package GasStationExeptions;

@SuppressWarnings("serial")
public class InvalidPumpException extends Exception {
	 public InvalidPumpException(int id) {
	        super("Cannot find FuelPump NO."+id);
	    }
}
