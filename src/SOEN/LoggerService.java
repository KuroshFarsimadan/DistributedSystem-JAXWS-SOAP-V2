package SOEN;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;

public class LoggerService {

	public String logger(String requestType, String requestParam, String requestState, String serverResponse) {

		// Check if the directory exists
		File dir = new File("SOEN_Server_Logs");
		boolean isCreatedFolder = dir.mkdir();

		// Check if file exists
		String fileName = "server_log";
		File tmpDir = new File(dir + "/" + fileName + ".txt");
		boolean exists = tmpDir.exists();

		// Curent server date and time
		Date date = new Date();
		SimpleDateFormat df = new SimpleDateFormat("MM/dd/YYYY HH:mm");
		String dateTime = "Logged request date: " + df.format(date);
		String logString = dateTime + " | Request type: " + requestType + " | Request parameter: " + requestParam
				+ " | Request state: " + requestState + " | Server response: " + serverResponse;
		try {
			if (exists == false) {
				BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + fileName + ".txt"));
				writer.newLine();
				writer.write(logString);
				writer.close();
			} else {
				BufferedWriter writer = new BufferedWriter(new FileWriter(dir + "/" + fileName + ".txt", true));
				// writer.append('\n'); // This only works if the file is read with an IDE
				writer.newLine();
				writer.append(logString);
				writer.close();
			}

		} catch (IOException e) {
			// Don't do anything yet. Future implementation for handling concurrent file
			// access and locking mechanism.
			System.out.println("Logger failure with exception " + e);
		}

		return logString;

	}

	public String getLogs() {
		return "logInformation";
	}
}
