package GasStationDAL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import GasStationBL.MainFuelPool;
import GasStationBL.GasStation;
import GasStationBL.WashingService;
import GasStationExeptions.InvalidPumpException;
import GasStationExeptions.StationClosedException;

public class ObjectLoader {

	public static ArrayList<GasStation> loadObject(File XmlFile)
			throws InvalidPumpException {
		ArrayList<GasStation> gasStations = new ArrayList<>();
		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(XmlFile);
			NodeList gasStationsNodes = doc.getElementsByTagName("GasStation");

			for (int i = 0; i < gasStationsNodes.getLength(); i++) {
				int numOfPumps = Integer.parseInt(gasStationsNodes.item(i)
						.getAttributes().getNamedItem("numOfPumps")
						.getNodeValue());
				float pricePerLiter = Float.parseFloat(gasStationsNodes.item(i)
						.getAttributes().getNamedItem("pricePerLiter")
						.getNodeValue());
				GasStation newGs = new GasStation(numOfPumps, pricePerLiter);
				gasStations.add(newGs);
				NodeList gsValues = gasStationsNodes.item(i).getChildNodes();
				for (int j = 0; i < gsValues.getLength()
						&& gsValues.item(j) != null; j++) {
					Node currentNode = gsValues.item(j);
					String curNodeName = currentNode.getNodeName();
					if (curNodeName == "MainFuelPool") {
						float maxCapacity = Float.parseFloat(currentNode
								.getAttributes().getNamedItem("maxCapacity")
								.getNodeValue());
						float currentCapacity = Float
								.parseFloat(currentNode.getAttributes()
										.getNamedItem("currentCapacity")
										.getNodeValue());
						MainFuelPool newFs = new MainFuelPool(maxCapacity,
								currentCapacity);
						newGs.setMainFuelPool(newFs);
					}
					if (curNodeName == "CleaningService") {
						int numOfTeams = Integer.parseInt(currentNode
								.getAttributes().getNamedItem("numOfTeams")
								.getNodeValue());
						float price = Float.parseFloat(currentNode
								.getAttributes().getNamedItem("price")
								.getNodeValue());
						int secondsPerAutoClean = Integer.parseInt(currentNode
								.getAttributes()
								.getNamedItem("secondsPerAutoClean")
								.getNodeValue());
						WashingService newWs = new WashingService(numOfTeams,
								price, secondsPerAutoClean);
						newGs.setWashingService(newWs);
					}
					if (curNodeName == "Cars") {
						NodeList cars = currentNode.getChildNodes();
						for (int k = 0; i < cars.getLength()
								&& cars.item(k) != null; k++) {
							if (cars.item(k).getNodeName() == "Car") {
								Node curCar = cars.item(k);
								int id = Integer.parseInt(curCar
										.getAttributes().getNamedItem("id")
										.getNodeValue());
								boolean wantCleaning;
								try {
									wantCleaning = Boolean.parseBoolean(curCar
											.getAttributes()
											.getNamedItem("wantCleaning")
											.getNodeValue());
								} catch (Exception e) {
									wantCleaning = false;
								}

								NodeList wantsFuel = curCar.getChildNodes();
								Boolean wantFuel = false;
								float WantFuelNumOfLiters = 0;

								for (int l = 0; l < wantsFuel.getLength()
										&& wantsFuel.item(l) != null; l++) {
									Node wantsFuelInfo = wantsFuel.item(l);
									if (wantsFuelInfo.getNodeName() == "WantsFuel") {
										WantFuelNumOfLiters = Float
												.parseFloat(wantsFuelInfo
														.getAttributes()
														.getNamedItem(
																"numOfLiters")
														.getNodeValue());
										wantFuel = true;
									}
								}
								newGs.addNewCar(id, wantCleaning, wantFuel,
										WantFuelNumOfLiters);
							}
						}
					}

				}

			}

		} catch (IOException | ParserConfigurationException | SAXException
				| StationClosedException e) {
			e.printStackTrace();
		}
		gasStations.get(0).init();

		return gasStations;

	}

}
