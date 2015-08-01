package GasStationDAL;

import java.sql.*;
import java.time.LocalDateTime;

public class dbConnect {
	static Connection connection = null;
	static ResultSet resultSet = null;
	static Statement statement = null;

	public static void closeDbConnection() throws SQLException {
		if (resultSet != null) {
			resultSet.close();
		}
		if (statement != null) {
			statement.close();
			if (connection != null) {
				connection.close();
			}
		}
	}

	public static void connectToDb() throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		String dbUrl = "jdbc:mysql://localhost/gasstationsso";
		connection = DriverManager.getConnection(dbUrl, "root", "");
		statement = connection.createStatement();
	}

	public static boolean insertCarToDb(int licensePlate) throws SQLException {
		if (checkIfExist("cars", "license_plate", licensePlate))
			return false;
		String sqlQuery = "INSERT INTO cars (license_plate) VALUES("
				+ licensePlate + ")";

		@SuppressWarnings("unused")
		int numOfAffectedRows = statement.executeUpdate(sqlQuery);
		return true;
	}

	public static boolean insertFuelPumpToDb(int fuelPumpId, int gasStationID)
			throws SQLException {
		if (checkIfExist("fuel_pumps", "fuel_pump_id", fuelPumpId))
			return false;
		String sqlQuery = "INSERT INTO fuel_pumps (fuel_pump_id, station_id) VALUES("
				+ fuelPumpId + ", " + gasStationID + ")";
		statement.executeUpdate(sqlQuery);
		return true;
	}

	public static boolean insertCleaningCrewsToDb(int crewId, int gasStationID)
			throws SQLException {
		if (checkIfExist("cleaning_crews", "cleaning_crew_id", crewId))
			return false;
		String sqlQuery = "INSERT INTO cleaning_crews (cleaning_crew_id, station_id) VALUES ("
				+ crewId + ", " + gasStationID + ")";
		int numOfAffectedRows = statement.executeUpdate(sqlQuery);
		System.out.println(numOfAffectedRows);
		return true;
	}

	public static boolean insertGasStationsToDb(int gasStationID,
			float pricePerLiter, int secondsPerAutoClean, float cleaningPrice)
			throws SQLException {
		if (checkIfExist("stations", "station_id", gasStationID))
			return false;
		String sqlQuery = "INSERT INTO stations (station_id, price_per_liter, price_per_wash) VALUES("
				+ gasStationID
				+ ", "
				+ pricePerLiter
				+ ", "
				+ cleaningPrice
				+ ")";

		@SuppressWarnings("unused")
		int numOfAffectedRows = statement.executeUpdate(sqlQuery);
		return true;
	}

	public static boolean insertCleaningTaskToDb(int licensePlate,
			int cleaningCrewId, LocalDateTime startTime) throws SQLException {
		String sqlQuery = "INSERT INTO wash_tasks (license_plate, cleaning_crew_id) VALUES("
				+ licensePlate + ", " + cleaningCrewId + ")";

		@SuppressWarnings("unused")
		int numOfAffectedRows = statement.executeUpdate(sqlQuery);
		return true;
	}

	public static boolean insertFuelingTaskToDb(int licensePlate,
			int fuelPumpId, float amountFueled, LocalDateTime startTime)
			throws SQLException {
		String sqlQuery = "INSERT INTO fuel_tasks (license_plate, fuel_pump_id, amount_fueled) VALUES("
				+ licensePlate + ", " + fuelPumpId + ", " + amountFueled + ")";

		@SuppressWarnings("unused")
		int numOfAffectedRows = statement.executeUpdate(sqlQuery);
		return true;
	}

	public static boolean checkIfExist(String table, String field, int value)
			throws SQLException {
		String sqlQuery = "SELECT COALESCE(MAX(" + field + "), -1) FROM "
				+ table + " WHERE " + field + " = " + value;
		resultSet = statement.executeQuery(sqlQuery);
		while (resultSet.next()) {
			if (resultSet.getInt(1) == -1)
				return false;
		}
		return true;
	}

	public static int getFuelingProfitFromCar(int licensePlate)
			throws SQLException, SQLTimeoutException {
		String sqlFuelQuery = "SELECT sum(ft.amount_fueled*s.price_per_liter) FROM cars c, fuel_tasks ft, fuel_pumps fp, stations s  WHERE (c.license_plate = "
				+ licensePlate
				+ " AND ft.license_plate = c.license_plate AND ft.fuel_pump_id = fp.fuel_pump_id AND fp.station_id = s.station_id)";
		resultSet = statement.executeQuery(sqlFuelQuery);
		int profitFromFuel = 0;
		while (resultSet.next()) {
			profitFromFuel = resultSet.getInt(1);
		}

		return profitFromFuel;
	}

	public static int getWashingProfitFromCar(int licensePlate)
			throws SQLException, SQLTimeoutException {
		String sqlFuelQuery = "SELECT (count(*)*s.price_per_wash) FROM cars c, wash_tasks wt, cleaning_crews cc, stations s  WHERE (c.license_plate = "
				+ licensePlate
				+ " AND wt.license_plate = c.license_plate AND wt.cleaning_crew_id = cc.cleaning_crew_id AND cc.station_id = s.station_id)";
		resultSet = statement.executeQuery(sqlFuelQuery);

		int profitFromWash = 0;
		while (resultSet.next()) {
			profitFromWash = resultSet.getInt(1);
		}
		return profitFromWash;
	}

	public static int getProfitFromPump(int pumpId) throws SQLException,
			SQLTimeoutException {
		String sqlFuelQuery = "SELECT sum(ft.amount_fueled*s.price_per_liter) FROM fuel_tasks ft, fuel_pumps fp, stations s  WHERE (ft.fuel_pump_id = "
				+ pumpId
				+ " AND ft.fuel_pump_id = fp.fuel_pump_id AND fp.station_id = s.station_id)";
		resultSet = statement.executeQuery(sqlFuelQuery);

		int profitFromPump = 0;
		while (resultSet.next()) {
			profitFromPump = resultSet.getInt(1);
		}
		return profitFromPump;
	}

	public static int getProfitFromFueling() throws SQLException,
			SQLTimeoutException {
		String sqlFuelQuery = "SELECT sum(ft.amount_fueled*s.price_per_liter) FROM fuel_tasks ft, fuel_pumps fp, stations s  WHERE (ft.fuel_pump_id = fp.fuel_pump_id AND fp.station_id = s.station_id)";
		resultSet = statement.executeQuery(sqlFuelQuery);

		int profitFromFueling = 0;
		while (resultSet.next()) {
			profitFromFueling = resultSet.getInt(1);
		}
		return profitFromFueling;
	}
	
	public static int getProfitFromWashing()
			throws SQLException, SQLTimeoutException {
		String sqlFuelQuery = "SELECT (count(*)*s.price_per_wash) FROM cars c, wash_tasks wt, cleaning_crews cc, stations s  WHERE (wt.cleaning_crew_id = cc.cleaning_crew_id AND cc.station_id = s.station_id)";
		resultSet = statement.executeQuery(sqlFuelQuery);

		int profitFromWash = 0;
		while (resultSet.next()) {
			profitFromWash = resultSet.getInt(1);
		}
		return profitFromWash;
	}

}
