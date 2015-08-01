package GasStationTasks;

import java.time.LocalDateTime;
import java.util.List;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

import GasStationBL.Car;
import GasStationBL.GasStation;
import GasStationBL.WhileCleaning;
import GasStationController.DataError;
import GasStationController.DataFunction;
import GasStationController.DataMessage;

public class WashTask extends AbstractTask {

	protected float cleanTime;
	protected int cleaningCrewId;

	public WashTask(Car car, GasStation context, int numOfTeams) {
		super(context);
		this.cleanTime = new Random().nextFloat() * 10;
		this.cleaningCrewId = new Random().nextInt(numOfTeams) + 1;
		this.car = car;
		doRandomTask();
	}

	public void doRandomTask() {
		try {
			List<Method> annotatedMethods = findAnnotatedMethods();
			int size = annotatedMethods.size();
			int randomNum = (new Random()).nextInt(size) + 1;
			Method method = annotatedMethods.get(randomNum - 1);
			String functionName = method.getName();
			context.showMessage(new DataFunction(car.getLicensePlate(),
					functionName));
		} catch (Exception e) {
			context.showMessage(new DataMessage("Error in random task selection while washing."));
		}

	}

	private List<Method> findAnnotatedMethods() throws NullPointerException,
			IllegalArgumentException, ClassCastException,
			UnsupportedOperationException {

		Method[] methods = Car.class.getMethods();
		List<Method> annotatedMethods = new ArrayList<Method>(methods.length);
		for (Method method : methods) {
			if (method.isAnnotationPresent(WhileCleaning.class)) {
				annotatedMethods.add(method);
			}
		}
		return annotatedMethods;
	}

	@Override
	public void run() {
		synchronized (car) {
			try {
				LocalDateTime startTime = LocalDateTime.now();

				context.showMessage(new DataMessage("Wash task Started for Car"
						+ car.getLicensePlate()));
				logger.log(Level.INFO,
						"Auto Clean Started for Car" + car.getLicensePlate()
								+ "Cleaning Time" + cleanTime * 1000, context);
				logger.log(Level.INFO, "Auto Clean Started Cleaning Time"
						+ cleanTime * 1000, car);
				Thread.sleep((long) cleanTime * 1000);
				logger.log(Level.INFO, "Auto Clean Endded in: " + cleanTime
						+ " for Car" + car.getLicensePlate(), context);
				logger.log(Level.INFO, "Auto Clean Endded in: " + cleanTime,
						car);

				ManulCleanTask mct = new ManulCleanTask(car, context,
						startTime, cleaningCrewId);
				context.getWashingService().setManualClean(mct);
				context.showMessage(new DataMessage("Wash task finished for Car"
						+ car.getLicensePlate()));
			} catch (InterruptedException e) {
				context.showMessage(new DataError("Error while washing car: "
						+ e.getMessage(),"Wash task"));
			}
		}

	}

}
