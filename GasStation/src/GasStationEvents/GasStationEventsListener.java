package GasStationEvents;

import GasStationExeptions.InvalidPumpException;
import GasStationExeptions.StationClosedException;

public interface GasStationEventsListener {
	void addcarModel(int licensePlate, Boolean wantCleaning,
			boolean wantFuel, float amount) throws InvalidPumpException, StationClosedException;
	void fillTheMainPumpModel(float amount);
	String getStatistics();
	void closeTheDayModel();
}
