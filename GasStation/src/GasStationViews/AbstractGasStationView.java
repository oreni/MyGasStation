package GasStationViews;

import GasStationEvents.GasStationEventsListener;

public interface AbstractGasStationView {

	void registerListener(GasStationEventsListener uiEventListener);
	void getException(Exception e);
	void showMessageFromModel(String message);

}
