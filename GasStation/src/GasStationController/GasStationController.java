package GasStationController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import GasStationBL.*;
import GasStationEvents.*;
import GasStationExeptions.InvalidPumpException;
import GasStationExeptions.StationClosedException;
import GasStationViews.AbstractGasStationView;

public class GasStationController extends Thread implements
		GasStationUiEventListener, GasStationEventsListener {
	private Vector<SocketData> allConnections = new Vector<SocketData>();

	private GasStation theStationModel;
	private AbstractGasStationView stationView;

	public GasStationController(AbstractGasStationView theView,
			GasStation station) {
		theStationModel = station;
		stationView = theView;

		theStationModel.registerListener(this);
		stationView.registerListener(this);
	}

	public void run() {
		try {
			@SuppressWarnings("resource")
			final ServerSocket server = new ServerSocket(7070);
			while (true) {
				final Socket socket = server.accept();
				new Thread(new Runnable() {

					@Override
					public void run() {
						SocketData sd = null;
						try {
							sd = new SocketData(socket);
							synchronized (allConnections) {
								allConnections.add(sd);
							}
							// sd.getOutputStream().writeObject(
							// new DataMessage("Connected"));
							Boolean stayConnected = true;
							do {
								Object obj = sd.getInputStream().readObject();
								if (obj == null)
									stayConnected = false;
								else {
									DataCar dataCar = null;
									String tmp1 = obj.getClass()
											.getSimpleName();
									String tmp2 = DataCar.class.getSimpleName();
									if (tmp1.equals(tmp2)) {
										dataCar = (DataCar) obj;
										addcarModel(dataCar.getLicensePlate(),
												dataCar.isWantsWashing(),
												dataCar.isWantsFuel(),
												dataCar.getFuelWanted());
									} else {
										messageToViewFromModel(new DataError(
												"Error, Please contact your administrator, Client recieved unknown object: "
														+ tmp2,
												"Error: Run Function (Client)"));
									}

								}
							} while (stayConnected);

						} catch (IOException | InvalidPumpException
								| StationClosedException
								| ClassNotFoundException e) {
							messageToViewFromModel(new DataMessage(
									"Error: Server disconnecting from "
											+ sd.getClientAddress() + e
											+ e.getMessage()));
						} finally {
							synchronized (allConnections) {
								allConnections.remove(socket);
							}
						}

					}

				}).start();
			}

		} catch (IOException e) {
			messageToViewFromModel(new DataError("Error: Failed server "
					+ e.getMessage(), "sending error"));
		} finally {
			synchronized (allConnections) {
				messageToViewFromModel(new DataMessage(
						"Server disconnecting all clients from "));
				for (SocketData connection : allConnections) {
					closeSocket(connection.getSocket());
					allConnections.remove(connection);
				}
			}
		}
	}

	private void sendBroadcastMessage(Object data) {
		synchronized (allConnections) {
			for (SocketData connection : allConnections) {
				try {
					if (connection.getSocket().isConnected()) {
						connection.getOutputStream().writeObject(data);
					}
				} catch (IOException e) {
					String objName = data.getClass().getSimpleName();
					if (objName.equals("DataError")) {
						System.out.println("here?????hjjhfj");
						System.out.println("error accured during sending "
								+ ((DataError) data).getMessage());
						stationView
								.showMessageFromModel("error accured during sending "
										+ ((DataError) data).getMessage());
					} else
						messageToViewFromModel(new DataError(
								"Error: Failed sending message to client "
										+ connection.getClientAddress()
										+ e.getMessage(), "sending error"));
				}
			}
		}

	}

	public void closeSocket(Socket socket) {
		try {
			if (socket.isConnected()) {
				socket.close();
			}

		} catch (IOException e) {
			messageToViewFromModel(new DataError("Error: While closing socket "
					+ e.getMessage(), "Close Socket"));
		}
	}

	@Override
	public void addcarModel(int licensePlate, Boolean wantWashing,
			boolean wantFuel, float numOfLitersWanted)
			throws InvalidPumpException, StationClosedException {
		theStationModel.addNewCar(licensePlate, wantWashing, wantFuel,
				numOfLitersWanted);
	}

	@Override
	public void fillTheMainPumpModel(float amount) {
		theStationModel.fillMainFuelPool(amount);
	}

	@Override
	public void closeTheDayModel() {
		theStationModel.closeStation();
	}

	@Override
	public void sendException(Exception e) {
		stationView.getException(e);
	}

	@Override
	public String getStatistics() {
		return theStationModel.getStatistics();
	}

	public void messageToViewFromModel(Object data) {
		String message;
		String objName = data.getClass().getSimpleName();
		switch (objName) {

		case "DataFunction":
			sendBroadcastMessage(data);
			break;

		case "DataMessage":
			message = ((DataMessage) data).getMessage();
			sendBroadcastMessage(data);
			stationView.showMessageFromModel(message);
			break;

		case "DataError":
			message = ((DataError) data).getMessage();
			sendBroadcastMessage(data);
			stationView.showMessageFromModel(message);
			break;

		// case "String":
		// message = (String) data;
		// stationView.showMessageFromModel(message);
		// break;

		default:
			messageToViewFromModel(new DataError(
					"Error, Please contact your administrator, trying to send unknown object",
					"sending unknown object"));
			break;

		}

	}

}
