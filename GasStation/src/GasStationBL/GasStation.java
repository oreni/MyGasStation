package GasStationBL;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import GasStationController.DataError;
import GasStationController.DataMessage;
import GasStationDAL.dbConnect;
import GasStationEvents.GasStationUiEventListener;
import GasStationExeptions.InvalidPumpException;
import GasStationExeptions.StationClosedException;
import GasStationLogging.GasStationFormat;
import GasStationTasks.*;

public class GasStation {

	public static final int GAS_STATION_ID = 1;
	private Vector<GasStationUiEventListener> listeners;
	private int numOfPumps;
	private float pricePerLiter;
	private MainFuelPool mainFuelPool;
	private WashingService washingService;
	private ArrayList<Car> carList;
	private ArrayList<FuelPump> fuelPumpList;
	private Handler logHandler;
	private static Logger logger = Logger.getLogger("GSLog");
	private Semaphore mainFuelPoolLock;
	private boolean stationActive;
	private Statistics gasStationStatistics;

	public GasStation(int numOfPumps, float pricePerLiter)
			 {

		this.numOfPumps = numOfPumps;
		this.pricePerLiter = pricePerLiter;
		carList = new ArrayList<Car>();

		fuelPumpList = new ArrayList<FuelPump>();
		for (int i = 1; i <= numOfPumps; i++) {
			FuelPump pump;
			try {
				pump = new FuelPump(i);
				fuelPumpList.add(pump);
			} catch (SecurityException | IOException e) {
				// TODO 
			}
			
		}

		try {
		mainFuelPoolLock = new Semaphore(numOfPumps, true);
		stationActive = true;
		listeners = new Vector<>();
		gasStationStatistics = new Statistics(this);
		
			logHandler = new FileHandler("logs/GasStation.xml");
		
		logHandler.setFormatter(new GasStationFormat());
		logger.addHandler(logHandler);
		logger.setUseParentHandlers(false);
		} catch (SecurityException | IOException e) {
			// TODO 
		}
	}

	public void init() {
		insertToDb(GAS_STATION_ID, pricePerLiter);
		createPumps();
		createCleaningCrews();
	}

	public void setMainFuelPool(MainFuelPool mainFuelPool) {
		this.mainFuelPool = mainFuelPool;
	}

	public void setWashingService(WashingService washingService) {
		this.washingService = washingService;
	}

	public void addNewCar(int licensePlate, Boolean wantWashing,
			boolean wantFuel, float numOfLitersWanted)
			throws InvalidPumpException, StationClosedException {
		if (!stationActive)
			throw new StationClosedException();
		Car car;
		try {
			car = new Car(licensePlate, wantWashing);
			insertCarToDb(licensePlate);
			if (wantFuel) {
				car.setWantFuel(wantFuel);
				car.setWantFuelNumOfLiters(numOfLitersWanted);
			}
			this.carList.add(car);
			moveToTasks(car);
		} catch (SecurityException e) {
			showMessage(new DataError("Error: problem with logger: "
					+ e.getMessage(), "Add new car"));
		} catch (IOException e) {
			showMessage(new DataError("Error: problem with logger: "
					+ e.getMessage(), "Add new car"));
		}
	}

	public void moveToTasks(Car car) throws InvalidPumpException {
		try {
			if (car.isWantFuel() && stationActive) {
				if (car.getFuelPumpNumWanted() <= 0)
					car.setFuelPumpWanted(findEmptyPump());
				FuelPump fuelPump = this.findFuelPumpByID(car
						.getFuelPumpNumWanted());
				FuelTask fuelTask = new FuelTask(car, this, fuelPump);
				fuelPump.fuel(fuelTask);
			} else if (car.isWantWashing() && stationActive) {
				WashTask washTask = new WashTask(car, this,
						washingService.getNumOfTeams());
				washingService.wash(washTask);
			} else {
				logger.log(Level.INFO, "Car" + car.getLicensePlate()
						+ " left the GasStation", car);

				car.dispose();

				carList.remove(car);
			}
		} catch (SecurityException e) {
			showMessage(new DataError("Error: problem with logger: "
					+ e.getMessage(), " Move to tasks"));
		}
	}

	public void registerListener(GasStationUiEventListener listener) {
		listeners.add(listener);
	}

	public FuelPump findFuelPumpByID(int fuelPumpId)
			throws InvalidPumpException {
		for (FuelPump fuelPump : fuelPumpList)
			if (fuelPump.getFuelPumpID() == fuelPumpId)
				return fuelPump;
		throw new InvalidPumpException(fuelPumpId);
	}

	public void fillMainFuelPool(float amount) {
		try {
			RefillMainFuelPoolTask refillMainFuelPoolTask = new RefillMainFuelPoolTask(
					amount, this);
			this.mainFuelPool.performTask(refillMainFuelPoolTask);
		} catch (NullPointerException e) {
			showMessage(new DataError(
					"Error: problem with Main Fuel Pool Task: "
							+ e.getMessage(), "fillMainFuelPool"));
		} catch (RejectedExecutionException e) {
			showMessage(new DataError(
					"Error: problem with Main Fuel Pool Task: "
							+ e.getMessage(), "fillMainFuelPool"));
		}
	}

	public WashingService getWashingService() {
		return washingService;
	}

	public Semaphore getMainFuelPoolLock() {
		return mainFuelPoolLock;
	}

	public MainFuelPool getMainFuelPool() {
		return mainFuelPool;
	}

	public int getNumOfPumps() {
		return numOfPumps;
	}

	public void closeStation() {
		try {
			stationActive = false;
			for (FuelPump fuelPump : fuelPumpList)
				fuelPump.close();
			mainFuelPool.close();
			mainFuelPoolLock.acquire(numOfPumps);
			washingService.close();
			mainFuelPoolLock.release(numOfPumps);
			showMessage(new DataMessage("Station Closed"));
		} catch (InterruptedException e) {
			mainFuelPoolLock.release(numOfPumps);
			throwException(e);
		}
		logHandler.close();
	}

	public void throwException(Exception e) {
		for (GasStationUiEventListener lis : listeners)
			lis.sendException(e);
	}

	public int findEmptyPump() {
		for (FuelPump fuelPump : fuelPumpList)
			if (fuelPump.isBusy() == false)
				return fuelPump.getFuelPumpID();
		return new Random().nextInt(numOfPumps) + 1;
	}

	public Statistics getGasStationStatistics() {
		return gasStationStatistics;
	}

	public float getPricePerLiter() {
		return pricePerLiter;
	}

	public void setPricePerLiter(float pricePerLiter) {
		this.pricePerLiter = pricePerLiter;
	}

	public void showMessage(Object data) {
		for (GasStationUiEventListener listener : listeners) {
			listener.messageToViewFromModel(data);
		}
	}

	public String getStatistics() {
		return gasStationStatistics.toString();
	}

	public WashingService getCleaningService() {
		return washingService;
	}

	private void insertCarToDb(int licensePlate) {
		try {
			dbConnect.connectToDb();
			dbConnect.insertCarToDb(licensePlate);
		} catch (SQLException | InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} finally {
			try {
				dbConnect.closeDbConnection();
			} catch (Exception e) {
				showMessage(new DataError("Cannot disconnect to database: "
						+ e.getMessage(), "DB connection"));
			}
		}
	}

	private void insertToDb(int gasStationId, float pricePerLiter2) {

		try {
			dbConnect.connectToDb();
			dbConnect.insertGasStationsToDb(GAS_STATION_ID, pricePerLiter,
					washingService.getSecondsPerAutoClean(),
					washingService.getPrice());
		} catch (InstantiationException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (IllegalAccessException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (ClassNotFoundException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (SQLException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} finally {
			try {
				dbConnect.closeDbConnection();
			} catch (Exception e) {
				showMessage(new DataError("Cannot disconnect to database: "
						+ e.getMessage(), "DB connection"));
			}
		}
	}

	private void createPumps() {
		try {
			dbConnect.connectToDb();
			for (FuelPump pump : fuelPumpList) {
				dbConnect.insertFuelPumpToDb(pump.getFuelPumpID(),
						GAS_STATION_ID);
			}
		} catch (InstantiationException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (IllegalAccessException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (ClassNotFoundException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (SQLException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} finally {
			try {
				dbConnect.closeDbConnection();
			} catch (Exception e) {
				showMessage(new DataError("Cannot disconnect to database: "
						+ e.getMessage(), "DB connection"));
			}
		}
	}

	private void createCleaningCrews() {
		try {
			dbConnect.connectToDb();

			for (int i = 1; i <= washingService.getNumOfTeams(); i++) {
				dbConnect.insertCleaningCrewsToDb(i, GasStation.GAS_STATION_ID);
			}
		} catch (InstantiationException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (IllegalAccessException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (ClassNotFoundException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} catch (SQLException e) {
			showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "DB connection"));
		} finally {
			try {
				dbConnect.closeDbConnection();
			} catch (SQLException e) {
				showMessage(new DataError("Cannot disconnect to database: "
						+ e.getMessage(), "DB connection"));
			}
		}
	}
}
