package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

import javax.swing.SwingUtilities;

import GasStationBL.Car;
import GasStationController.DataCar;
import GasStationController.DataError;
import GasStationController.DataFunction;
import GasStationController.DataMessage;

public class Client extends Thread {
	private Socket socket = null;
	private ObjectOutputStream toNetOutputStream;
	private ObjectInputStream FromNetInputStream;
	UiForClient clientGui;

	public Client(UiForClient uiForClient) {
		this.clientGui = uiForClient;
	}

	public boolean connect() {
		try {
			socket = new Socket("localhost", 7070);
			clientGui.showMessage("Client shoko connecting to: "
					+ socket.getLocalAddress());

			toNetOutputStream = new ObjectOutputStream(socket.getOutputStream());
			FromNetInputStream = new ObjectInputStream(socket.getInputStream());

		} catch (IOException e) {
			clientGui.showErrorMessage(e.getMessage(),
					"Error: Connect Function");
			if (socket != null)
				if (socket.isConnected())
					try {
						socket.close();
					} catch (IOException ex) {
						clientGui.showErrorMessage(ex.getMessage(),
								"Error: Connect Function");
					} catch (NullPointerException ex) {
						clientGui.showErrorMessage(ex.getMessage(),
								"Error: Connect Function (socket failed)");
					}
			return false;
		}
		return true;

	}

	public void sendData(int licensePlate, float fuelWanted,
			boolean wantsWashing) {
		try {
			boolean wantsFuel = false;
			if (fuelWanted > 0)
				wantsFuel = true;

			DataCar car = new DataCar(licensePlate, wantsWashing, wantsFuel,
					fuelWanted);

			toNetOutputStream.writeObject(car);
		} catch (IOException e) {
			clientGui.showErrorMessage(e.getMessage(),
					"Error: Send Data Function");
		}
	}

	public void run() {
		try {// client listening

			boolean stayConnected = true;

			do {
				final Object data = FromNetInputStream.readObject();

				if (data == null) {
					stayConnected = false;
				} else {
					String objName = data.getClass().getSimpleName();
					switch (objName) {

					case "DataFunction":
						String functionName = ((DataFunction) data)
								.getFunctionName();
						int licensePlate = ((DataFunction) data)
								.getLicensePlate();
						Method function = Car.class.getMethod(functionName);
						Car car = new Car(licensePlate, false);
						function.invoke(car);
						break;

					case "DataMessage":

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								String message = ((DataMessage) data)
										.getMessage();
								clientGui.sendTextToTextArea(message);
							}
						});

						break;

					case "DataError":

						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								String message = ((DataError) data)
										.getMessage();
								String title = ((DataError) data).getTitle();
								clientGui.showErrorMessage(
										"recieved from server: " + message,
										title);
							}
						});
						break;

					default:
						clientGui.showErrorMessage(
								"Error, Please contact your administrator, Client recieved unknown object: "
										+ objName,
								"Error: Run Function (Client)");
						break;
					}
				}
			} while (stayConnected);
		} catch (IOException | InvocationTargetException
				| IllegalAccessException | IllegalArgumentException
				| NoSuchMethodException | SecurityException
				| ClassNotFoundException | NullPointerException e) {
			clientGui.showErrorMessage(e.getMessage(),
					"Error: Run Function (Client)");
		} finally {
			try {
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				clientGui.showErrorMessage(e.getMessage(),
						"Error: Run Function (Client)2");
			}
			clientGui.showMessage("Client disconnected");
		}
	}

	public void close() {

		if (socket != null)
			if (socket.isConnected())
				try {
					socket.close();
				} catch (IOException ex) {
					clientGui.showErrorMessage(ex.getMessage(),
							"Error: Connect Function");
				} catch (NullPointerException ex) {
					clientGui.showErrorMessage(ex.getMessage(),
							"Error: Connect Function (socket failed)");
				}
	}
}
