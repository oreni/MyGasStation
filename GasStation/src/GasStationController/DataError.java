package GasStationController;

import java.io.Serializable;

public class DataError implements Serializable {

	private static final long serialVersionUID = 4L;
	private String message;
	private String title;

	public DataError(String theMessage, String title) {
		this.message = theMessage;
		this.title = title;
	}

	public String getMessage() {
		return message;
	}
	
	public String getTitle() {
		return title;
	}

}
