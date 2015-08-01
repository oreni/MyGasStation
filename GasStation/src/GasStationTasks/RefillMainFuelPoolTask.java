package GasStationTasks;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;

import GasStationBL.MainFuelPool;
import GasStationBL.GasStation;
import GasStationController.DataMessage;
import GasStationExeptions.MainFuelStockOverFlow;

public class RefillMainFuelPoolTask extends AbstractTask {

	private float fuelAmount;
	private Semaphore mainFuelPoolLock;
	private MainFuelPool fuelStock;
	private int numOfPumps;

	public RefillMainFuelPoolTask(float fuelAmount,GasStation context) {
		super(context);
		this.fuelAmount = fuelAmount;
		this.mainFuelPoolLock = context.getMainFuelPoolLock();
		this.fuelStock = context.getMainFuelPool();
		this.numOfPumps = context.getNumOfPumps();
	}

	@Override
	public void run() {
		float fuelingTime = new Random().nextInt(210);
		try {
			context.showMessage(new DataMessage("Main fuel pool refill task Started"));
			mainFuelPoolLock.acquire(numOfPumps);
			logger.log(Level.INFO, "refuel the main Fuel Stock Started", fuelStock);
			Thread.sleep((long) fuelingTime);
			fuelStock.addFuel(fuelAmount);
			context.getGasStationStatistics().addMainFuelingInfo(fuelingTime, fuelAmount);
			logger.log(Level.INFO, "refuel the main Fuel Stock Endded", fuelStock);
			mainFuelPoolLock.release(numOfPumps);
			context.showMessage(new DataMessage("Main fuel pool refill task finished"));
		} catch (InterruptedException e) {
			mainFuelPoolLock.release(numOfPumps);
			context.throwException(e);
		} catch (MainFuelStockOverFlow e) {
			mainFuelPoolLock.release(numOfPumps);
			context.getGasStationStatistics().addMainFuelingInfo(fuelingTime, e.amount);
			context.throwException(e);
		}
	}
}
