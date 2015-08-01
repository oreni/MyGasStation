package GasStationLogging;

import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class GasStationFormat extends Formatter {
	@Override
	public String format(LogRecord record) {
		DateTimeFormatter date = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		return date + "-->"+ record.getLevel()+ ":" + record.getMessage() + ":)\r\n";
	}

}
