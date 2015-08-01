package GasStationEvents;

public interface GasStationUiEventListener {
	public void sendException(Exception e);

	public void messageToViewFromModel(Object data);
}
