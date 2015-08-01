package GasStationTasks;

import java.util.logging.Logger;

import GasStationBL.Car;
import GasStationBL.GasStation;

public abstract class AbstractTask implements Runnable {

	protected Car car;
	protected GasStation context;
	protected static Logger logger = Logger.getLogger("GSLog");
	
	protected AbstractTask(GasStation context){
		this.context = context;
	}
	
}
