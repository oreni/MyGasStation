package GasStationController;

import java.io.Serializable;

public class DataMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	String message;

	public DataMessage(String theMessage) {
		this.message = theMessage;
	}

	public String getMessage() {
		return message;
	}

}
