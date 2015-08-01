package GasStationViews;

import java.io.File;

import GasStationBL.GasStation;
import GasStationController.GasStationController;
import GasStationDAL.ObjectLoader;
import GasStationExeptions.InvalidPumpException;

public class Runner {

	public static void main(String[] args) throws InvalidPumpException {
		GasStation g = ObjectLoader.loadObject(new File("test.xml")).get(0);
		ConsoleUI ui = new ConsoleUI();

		@SuppressWarnings("unused")
		GasStationController gasControl = new GasStationController(ui, g);

		ui.showMenu();

	}

}
