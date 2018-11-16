package SOEN;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.RemoteException;
import javax.xml.ws.Endpoint;

public class Server {

	public static void main(String[] args) throws RemoteException {

		/*
		 * 
		 * SOEN - RMI server starts at 8080 - UDP server starts at 8081
		 * 
		 * INSE - RMI server starts at 9090 - UDP server starts at 9091
		 * 
		 * COMP - RMI server starts at 10010 - UDP server starts at 10011
		 */
		ServerImpl exportedObj = new ServerImpl();

		Runnable task = () -> {
			startWebService(args, exportedObj, 8080);
		};

		Runnable task2 = () -> {
			try {
				startUDP(8081, exportedObj);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		};

		Thread thread = new Thread(task);
		Thread thread2 = new Thread(task2);

		thread.start();
		thread2.start();

	}

	/**
	 * UDP Server related configurations from this point onward.
	 * 
	 * @param exportedObj
	 * 
	 * @param port
	 */
	// This method starts the UDP service
	private synchronized static void startUDP(int portVar, ServerImpl exportedObj) throws RemoteException {
		DatagramSocket aSocket = null;
		try {
			aSocket = new DatagramSocket(portVar);
			byte[] buffer = new byte[1000];
			System.out.println("UDP Server ready at port " + portVar);

			while (true) {

				// The three lines of code below are responsible for blocking the UDP method
				// until a new client request is received.
				DatagramPacket request = new DatagramPacket(buffer, buffer.length);
				aSocket.receive(request);
				String requestData = request.getData() + "";

				System.out.println("startUDP: " + portVar + " is " + new String(request.getData()));

				System.out.println("Client sent " + new String(request.getData()));

				// I want to get the data again in another byte array so that I will actually
				// send the right String value after receiving it.
				byte[] bufferCopy = new byte[request.getLength()];

				// Copies an array from the specified source array, beginning at the specified
				// position, to the specified position of the destination array.
				System.arraycopy(request.getData(), request.getOffset(), bufferCopy, 0, request.getLength());

				// Creating a new string for the copy
				String bufferData = new String(bufferCopy);
				String stringMessage = "";

				try {
					if (bufferData.equalsIgnoreCase("winter") || bufferData.equalsIgnoreCase("summer")
							|| bufferData.equalsIgnoreCase("spring") || bufferData.equalsIgnoreCase("fall")) {
						stringMessage = "" + exportedObj.semesterCourses(bufferData);
					} else {
						// 0 = switch method (methodName), 1 = studentID, 2 = newCourseID, 3 =
						// oldCourseID
						String[] splitData = bufferData.split("\\s+");

						if (bufferData.contains("removeStudentCourse")) {
							stringMessage = ""
									+ exportedObj.removeStudentCourse(splitData[1], splitData[2], splitData[3]);
						} else if (bufferData.contains("addStudentCourse")) {
							stringMessage = "" + exportedObj.addStudentCourse(splitData[1], splitData[2], splitData[3]);
						} else if (bufferData.contains("revertRemove")) {
							// studentid, courseid, semester
							stringMessage = "" + exportedObj.revertRemove(splitData[1], splitData[2]);
						} else if (bufferData.contains("revertAdd")) {
							// studentid, courseid, semester
							stringMessage = "" + exportedObj.revertAdd(splitData[1], splitData[2]);
						}

					}
				} catch (Exception e) {
					stringMessage = "An error occurred: " + e;
				}

				byte[] message = stringMessage.getBytes();

				DatagramPacket reply = new DatagramPacket(message, message.length, request.getAddress(),
						request.getPort());

				aSocket.send(reply);
			}
		} catch (SocketException e) {
			System.out.println("Socket error: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("IO error: " + e.getMessage());
		}
		// We don't want to close the UDP connections
		finally {
			if (aSocket != null)
				aSocket.close();
		}

	}

	// This method starts the RMI service
	private static void startWebService(String[] args, ServerImpl exportedObj, int port) {
		try {
			Endpoint.publish("http://localhost:" + port + "/ws/server", exportedObj);
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}
	}

}
