package GasStationBL;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import GasStationExeptions.MainFuelStockEmptyException;
import GasStationExeptions.MainFuelStockLowException;
import GasStationExeptions.MainFuelStockOverFlow;
import GasStationTasks.AbstractTask;

public class MainFuelPool {
	private float maxCapacity;
	private float currentCapacity;
	private ExecutorService jobExecutorService;
	private final float redLine = 0.2f;

	public MainFuelPool(float maxCapacity, float currentCapacity) {
		this.maxCapacity = maxCapacity;
		this.currentCapacity = currentCapacity;
		this.jobExecutorService = Executors.newSingleThreadExecutor();
	}

	public synchronized double getfuelAmount() {
		return currentCapacity;
	}

	public synchronized void pumpFuel(float numOfLitersWanted)
			throws MainFuelStockLowException, MainFuelStockEmptyException {
		if (currentCapacity - numOfLitersWanted < 0) {
			float numOfLitersGiven = currentCapacity;
			currentCapacity = 0;
			throw new MainFuelStockEmptyException(numOfLitersGiven);
		}
		currentCapacity -= numOfLitersWanted;
		if (currentCapacity / maxCapacity <= redLine)
			throw new MainFuelStockLowException(currentCapacity);
	}

	public synchronized void addFuel(float amount) throws MainFuelStockOverFlow {
		if (amount + currentCapacity > maxCapacity) {
			float diff = maxCapacity - currentCapacity;
			currentCapacity = maxCapacity;
			throw new MainFuelStockOverFlow(diff);
		} else
			currentCapacity += amount;
	}

	public void performTask(AbstractTask rmft) throws NullPointerException,
			RejectedExecutionException {
		jobExecutorService.execute(rmft);
	}

	public void close() throws InterruptedException, SecurityException {
		jobExecutorService.shutdown();
		jobExecutorService.awaitTermination(Long.MAX_VALUE,
				TimeUnit.NANOSECONDS);
	}

}
