package GasStationBL;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import GasStationLogging.GasStationFilter;
import GasStationLogging.GasStationFormat;
import GasStationTasks.WashTask;
import GasStationTasks.ManulCleanTask;

public class WashingService {
	private int numOfTeams;
	private float price;
	private int secondsPerAutoWash;
	private Handler logHandler;
	private static Logger logger = Logger.getLogger("GSLog");
	private ExecutorService machine;
	private ExecutorService cleaningCrews;

	public WashingService(int numOfTeams, float price, int secondsPerAutoClean)
			throws SecurityException, IOException {
		this.numOfTeams = numOfTeams;
		this.price = price;
//		this.secondsPerAutoWash = secondsPerAutoWash;
		logHandler = new FileHandler("logs/WashServices/WashService.xml");
		logHandler.setFilter(new GasStationFilter(this));
		logHandler.setFormatter(new GasStationFormat());
		logger.addHandler(logHandler);
		machine = Executors.newSingleThreadExecutor();
		cleaningCrews = Executors.newFixedThreadPool(numOfTeams);
	}

	public void wash(WashTask ct) throws RejectedExecutionException,
			NullPointerException {
		machine.execute(ct);
	}

//	@Override
//	public String toString() {
//		return "WashService [numOfTeams=" + numOfTeams + ", price=" + price
//				+ ", secondsPerAutoClean=" + secondsPerAutoWash + "]";
//	}

	public int getSecondsPerAutoClean() {
		return secondsPerAutoWash;
	}

	public void setManualClean(ManulCleanTask mct)
			throws RejectedExecutionException, NullPointerException {
		//TODO
		if(!cleaningCrews.isShutdown())
			cleaningCrews.execute(mct);
	}

	public void close() throws SecurityException {
		cleaningCrews.shutdown();
		machine.shutdown();
		logHandler.close();
	}

	public float getPrice() {
		return price;
	}

	public int getNumOfTeams() {
		return numOfTeams;
	}

	

}
