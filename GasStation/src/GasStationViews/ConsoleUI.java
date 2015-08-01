package GasStationViews;
import java.util.LinkedList;
import java.util.Scanner;

import GasStationEvents.GasStationEventsListener;

public class ConsoleUI implements AbstractGasStationView {

	private static Scanner s = new Scanner(System.in);
	private  LinkedList<GasStationEventsListener> allListeners = new LinkedList<GasStationEventsListener>();
	
	public  void showMenu() {
		String swValue;
		boolean exit = false;
		while (!exit) {
			// Display menu graphics
			System.out.println("===========================================================");
			System.out.println("|   Gas Station MENU                                      |");
			System.out.println("===========================================================");
			System.out.println("| Options:                                                |");
			System.out.println("|        1. Add car                                       |");
			System.out.println("|        2. add Fuel to MainFuelPool                      |");
			System.out.println("|        3. Show statistics                               |");
			System.out.println("|        4. Shutdown the gas station and show statistics  |");
			System.out.println("|        5. Exit                                          |");
			System.out.println("===========================================================");
			swValue = s.next();
			// Switch construct
			switch (swValue) {
			case "1":
				addNewCar();
				break;
			case "2":
				fillMainFuelPool();
				break;
			case "3":
				showStatistics();
				break;
			case "4":
				CloseTheStation();
				showStatistics();
				exit=true;
				break;
			case "5":
				exit=true;
				break;
			default:
				System.out.println("Invalid selection");
				break; // This break is not really necessary
			}
		}
	}

	@Override
	public void registerListener(GasStationEventsListener uiEventListener) {
		allListeners.add(uiEventListener);

	}

	@Override
	public void getException(Exception e) {
		System.out.println("**********");
		System.out.println("Error-->"+e.getMessage());
		System.out.println("**********");
		
	}
	
	public void fillMainFuelPool(){
		try {
			System.out.println("Please enter the amout to fill");
			float amount =s.nextFloat();
			for (GasStationEventsListener lis:allListeners)
				lis.fillTheMainPumpModel(amount);
		} catch (Exception e) {
			getException(e);
		}
	}
	
	public void CloseTheStation(){
		for (GasStationEventsListener lis:allListeners)
			lis.closeTheDayModel();
	}
	
	public void addNewCar(){

		try {
			System.out.println("Please enter the Car id");
			int carId =s.nextInt();
			System.out.println("Tasks:0-Nothing,1-Fuel,2-Cleaning,3-Both");
			float task =s.nextInt();
			Boolean wantCleaning = false;
			Boolean wantFuel = false;
			if(task<0 || task>3)
				throw new Exception("iligal number");
			
			if(task==2 || task==3)
				wantCleaning = true;
			else
				wantCleaning = false;
			
			float amount = 0;
			if(task==1 || task==3)
			{
				System.out.println("Please enter Wanted amout of Fuel int Liters");
				amount =s.nextFloat();
				wantFuel = true;
			}
			for (GasStationEventsListener lis:allListeners)
				lis.addcarModel(carId,wantCleaning,wantFuel,amount);

		} catch (Exception e) {
			getException(e);
		}
		
	}
	
	public void showStatistics(){
		String stats = "";
		for (GasStationEventsListener lis : allListeners)
			stats = lis.getStatistics();
		System.out.println(stats);

	}
	
	public void showMessageFromModel(String message){
		System.out.println(message);
	}
	
	
}
