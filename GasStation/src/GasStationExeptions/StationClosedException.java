package GasStationExeptions;

@SuppressWarnings("serial")
public class StationClosedException extends Exception {
	 public StationClosedException() {
	        super("The Station is Closed");
	    }
}
