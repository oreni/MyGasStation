package GasStationBL;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import GasStationLogging.GasStationFilter;
import GasStationLogging.GasStationFormat;
import GasStationTasks.FuelTask;

public class FuelPump {

	private int fuelPumpId;
	private Handler logHandler;
	private static Logger logger = Logger.getLogger("GSLog");
	private float fuelTimePerLiterInSeconds;
	private ExecutorService thePump;
	private boolean busy;

	public FuelPump(int fuelPumpId) throws SecurityException, IOException {
		this.fuelPumpId = fuelPumpId;
		logHandler = new FileHandler("logs/FuelPumps/FuelPump-" + fuelPumpId
				+ ".xml");
		logHandler.setFilter(new GasStationFilter(this));
		logHandler.setFormatter(new GasStationFormat());
		logger.addHandler(logHandler);
		fuelTimePerLiterInSeconds = new Random().nextInt(10);
		thePump = Executors.newSingleThreadExecutor();
	}

	public int getFuelPumpID() {
		return this.fuelPumpId;
	}

	public float getFuelTimePerLiterSec() {
		return fuelTimePerLiterInSeconds;
	}

	public void fuel(FuelTask fuelTask) throws RejectedExecutionException,
			NullPointerException {
		thePump.execute(fuelTask);
	}

	public synchronized boolean isBusy() {
		return busy;
	}

	public synchronized void setBusy(boolean busy) {
		this.busy = busy;
	}

	public void close() throws InterruptedException, SecurityException {
		thePump.shutdown();
		thePump.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		logHandler.close();
	}
}
