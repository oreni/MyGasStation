package GasStationTasks;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import GasStationBL.Car;
import GasStationBL.FuelPump;
import GasStationBL.MainFuelPool;
import GasStationBL.GasStation;
import GasStationController.DataError;
import GasStationController.DataMessage;
import GasStationDAL.dbConnect;
import GasStationExeptions.InvalidPumpException;
import GasStationExeptions.MainFuelStockEmptyException;
import GasStationExeptions.MainFuelStockLowException;

public class FuelTask extends AbstractTask {

	private float fuelTimePerLiterSec;
	private Semaphore MainFuelPoolLock;
	private MainFuelPool fuelStock;
	private FuelPump fuelPump;
	private LocalDateTime startTime;
	
	public FuelTask(Car car,GasStation context,FuelPump fuelPump) throws InvalidPumpException {
		super(context);
		this.car = car;
		this.MainFuelPoolLock = context.getMainFuelPoolLock();
		this.fuelStock = context.getMainFuelPool();
		this.fuelPump =  fuelPump; 
		this.fuelTimePerLiterSec = fuelPump.getFuelTimePerLiterSec();
	}

	@Override
	public void run() {
		float fuelingTime = fuelTimePerLiterSec* car.getWantFuelNumOfLiters();
		synchronized (car) {
			context.showMessage(new DataMessage("Fuel task started for car: " + car.getLicensePlate()+" Fueling time "+fuelingTime));
			try {
				startTime = LocalDateTime.now();
				fuelPump.setBusy(true);
				MainFuelPoolLock.acquire();
				logger.log(Level.INFO, "Fuel task started for car" + car.getLicensePlate()+" Fueling time "+fuelingTime, fuelPump);
				logger.log(Level.INFO, "Fuel task started fueling time "+fuelingTime, car);
				fuelStock.pumpFuel( car.getWantFuelNumOfLiters());
				Thread.sleep((long) fuelingTime);
				
				MainFuelPoolLock.release();
				addToFuelTask();
				context.showMessage(new DataMessage("Fuel task finished for car: " + car.getLicensePlate()+" Fueling time "+fuelingTime));
				context.getGasStationStatistics().addFuelingInfo(fuelingTime, car.getWantFuelNumOfLiters());
			} catch (InterruptedException e) {
				context.throwException(e);
			} catch(MainFuelStockEmptyException e){
				context.getGasStationStatistics().addFuelingInfo(fuelingTime, e.amount);
				context.throwException(e);
			} catch( MainFuelStockLowException e){
				context.getGasStationStatistics().addFuelingInfo(fuelingTime, e.amount);
				context.throwException(e);
				
			}finally
	        {
				logger.log(Level.INFO, "Fuel task ended in: "+fuelingTime+" for Car" + car.getLicensePlate(), fuelPump);
				logger.log(Level.INFO, "Fuel task ended in: "+fuelingTime, car);
				MainFuelPoolLock.release();
				fuelPump.setBusy(false);
				car.setWantFuel(false);
				try {
					context.moveToTasks(car);
				} catch (InvalidPumpException e) {
					context.throwException(e);
				}
	        }
		}
	}

	private void addToFuelTask() {
		try {
			dbConnect.connectToDb();
			dbConnect.insertFuelingTaskToDb(car.getLicensePlate(), fuelPump.getFuelPumpID(), car.getWantFuelNumOfLiters(), startTime);
			
		} catch (InstantiationException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding Fuel task to DB"));
		} catch (SQLException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding Fuel task to DB"));
		} catch (IllegalAccessException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding Fuel task to DB"));
		} catch (ClassNotFoundException e) {
			context.showMessage(new DataError("Cannot connect to database: "
					+ e.getMessage(), "adding Fuel task to DB"));
		} finally {
			try {
				dbConnect.closeDbConnection();
			} catch (Exception e) {
				context.showMessage(new DataError("Cannot disconnect from database: "
						+ e.getMessage(), "adding Fuel task to DB"));
			}
		}
	}
}
