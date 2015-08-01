package GasStationController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

public class SocketData {

	private Socket socket;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private String clientAddress;

	public SocketData(Socket socket) throws IllegalBlockingModeException,
			IOException, NullPointerException {
		this.socket = socket;

		this.outputStream = new ObjectOutputStream(socket.getOutputStream());
		this.inputStream = new ObjectInputStream(socket.getInputStream());

		clientAddress = socket.getInetAddress() + ":" + socket.getPort();
	}

	public Socket getSocket() {
		return socket;
	}

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}

	public String getClientAddress() {
		return clientAddress;
	}

}
