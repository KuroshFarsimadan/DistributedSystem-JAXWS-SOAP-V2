package INSE;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import Interfaces.ServerInterface;

/**
 * 
 * @author Kurosh: Instead of using JUnit or other testing framework, I thought
 *         that this type of testing would be faster. If the application would
 *         be larger, then it would be good to use JUnit.
 */
public class Test {

	// So that we can only change one field if the server is different (different
	// identifier)
	private final static String departmentIdentifier = "SOEN";

	public static void main(String args[]) throws MalformedURLException, RemoteException {
		testMethods();
	}

	protected static void testMethods() throws MalformedURLException, RemoteException {
		URL wsdlURL = new URL("http://localhost:9090/ws/server");

		QName qname = new QName("http://INSE/", "ServerImplService");

		Service service = Service.create(wsdlURL, qname);

		ServerInterface exportedObj = service.getPort(ServerInterface.class);

		System.out.println("Testing begins...");

		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3355", departmentIdentifier + "1234", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3355", departmentIdentifier + "1234", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3355", departmentIdentifier + "1234", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3355", departmentIdentifier + "1234", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3322", departmentIdentifier + "1234", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S32322", departmentIdentifier + "1234", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S2255", departmentIdentifier + "1234", "FALL"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3355", departmentIdentifier + "123444", "WINTER"));

		System.out.println("");

		String message = exportedObj.addCourse(departmentIdentifier + "883", "WINTER");
		if (message.toString().equalsIgnoreCase("A new course SOEN883 has been added for WINTER semester")) {
			System.out.println("Adding a new course was successful with the message: " + message);
		} else {
			System.out.println("Test case failed with the message: " + message);
		}

		System.out.println("");

		message = exportedObj.addCourse(departmentIdentifier + "883", "WINTER");
		if (message.toString().equalsIgnoreCase("A new course SOEN883 has been added for WINTER semester")) {
			System.out.println("Adding a new course was successful with the message: " + message);
		} else {
			System.out.println("Test case failed with the message: " + message);
		}

		System.out.println("");

		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3355", departmentIdentifier + "883", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S332", departmentIdentifier + "883", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3445", departmentIdentifier + "883", "WINTER"));
		System.out.println(
				exportedObj.enrolCourse(departmentIdentifier + "S3245", departmentIdentifier + "883", "WINTER"));

		System.out.println("");

		System.out.println(exportedObj.getClassSchedule(departmentIdentifier + "S3355"));

		System.out.println("");

		System.out.println(exportedObj.dropCourse(departmentIdentifier + "S3245", departmentIdentifier + "883"));
		System.out.println(exportedObj.dropCourse(departmentIdentifier + "S3355", departmentIdentifier + "883"));
		System.out.println(exportedObj.dropCourse(departmentIdentifier + "S3355", departmentIdentifier + "883"));

		System.out.println("");

		System.out.println(exportedObj.getClassSchedule(departmentIdentifier + "S3355"));

		System.out.println("");

		System.out.println(exportedObj.removeCourse(departmentIdentifier + "883", "WINTER"));
		System.out.println(exportedObj.removeCourse(departmentIdentifier + "883", "WINTER"));

		System.out.println("");

		System.out.println(exportedObj.getClassSchedule(departmentIdentifier + "S3355"));

		System.out.println("Course availability " + exportedObj.listCourseAvailability("WINTER"));

	}

}
