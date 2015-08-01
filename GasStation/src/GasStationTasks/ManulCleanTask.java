package GasStationTasks;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.logging.Level;

import GasStationBL.Car;
import GasStationBL.GasStation;
import GasStationController.DataError;
import GasStationController.DataMessage;
import GasStationDAL.dbConnect;
import GasStationExeptions.InvalidPumpException;

public class ManulCleanTask extends AbstractTask {

	private float cleanTime;
	private int cleaningCrewId;
	private LocalDateTime startTime;

	public ManulCleanTask(Car car, GasStation context, LocalDateTime startTime,
			int cleaningCrewId) {
		super(context);
		this.cleanTime = new Random().nextFloat() * 10;
		this.car = car;
		this.cleaningCrewId = cleaningCrewId;
		this.startTime = startTime;
	}

	@Override
	public void run() {
		synchronized (car) {
			try {
				context.showMessage(new DataMessage("manual clean task started for car: "
						+ car.getLicensePlate()));
				logger.log(Level.INFO,
						"Manual clean started for car" + car.getLicensePlate(),
						context);
				logger.log(Level.INFO, "Manual clean started", car);
				Thread.sleep((long) cleanTime * 100);
				logger.log(Level.INFO, "Manual clean ended in: " + cleanTime
						+ " for Car" + car.getLicensePlate(), context);
				logger.log(Level.INFO, "Manual clean ended in: " + cleanTime,
						car);
				context.getGasStationStatistics().addWashingInfo(
						cleanTime * 100);
				car.setWantWashing(false);
				context.moveToTasks(car);
				addToCleanTask();
				context.showMessage(new DataMessage("manual clean task finished for car: "
						+ car.getLicensePlate()));
			} catch (InterruptedException e) {
				context.throwException(e);
			} catch (InvalidPumpException e) {
				context.throwException(e);
			}
		}

	}

	private void addToCleanTask() {
		try {
			dbConnect.connectToDb();
			dbConnect.insertCleaningTaskToDb(car.getLicensePlate(),
					cleaningCrewId, startTime);
		} catch (InstantiationException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding manual Washing task to DB"));
		} catch (SQLException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding manual Washing task to DB"));
		} catch (IllegalAccessException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding manual Washing task to DB"));
		} catch (ClassNotFoundException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding manual Washing task to DB"));
		} finally {
			try {
				dbConnect.closeDbConnection();
			} catch (Exception e) {
				context.showMessage(new DataError("Cannot disconnect from database: "
						+ e.getMessage(), "adding manual Washing task to DB"));
			}
		}
	}
}
