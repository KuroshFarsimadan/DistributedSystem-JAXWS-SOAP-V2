package INSE;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import Interfaces.ServerInterface;

import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

/**
 * @author Kurosh Farsi Madan
 *
 */
@WebService(endpointInterface = "Interfaces.ServerInterface")
public class ServerImpl implements ServerInterface {

	private HashMap<String, HashMap> courseRecords = new HashMap<String, HashMap>();

	private HashMap<String, Integer> departmentToPort = new HashMap<String, Integer>();

	private ServerImplHelper sih = new ServerImplHelper();

	// So that we can only change one field if the server is different (different
	// identifier)
	private final String departmentIdentifier = "INSE";

	// Logger service for logging the server requests
	private LoggerService s = new LoggerService();

	public ServerImpl() {
		super();
		initDatabase();
	}

	/************************************************************************************************
	 * Methods for init
	 ************************************************************************************************/

	private void initDatabase() {
		departmentToPort.put("SOEN", 8081);
		departmentToPort.put("INSE", 9091);
		departmentToPort.put("COMP", 10011);

		// Each term can have ..* courses
		HashMap<String, HashMap> course = new HashMap<String, HashMap>();

		// Each course can have ..* course information fields
		HashMap<String, HashMap<String, String>> courseInformation = new HashMap<String, HashMap<String, String>>();

		// Each course can have 0..* students
		HashMap<String, String> students = new HashMap<String, String>();
		students.put(departmentIdentifier + "S1234", "Kurosh Farsimadan");

		courseInformation.put("Capacity", new HashMap<String, String>());
		courseInformation.get("Capacity").put("TotalCapacity", "3");
		courseInformation.get("Capacity").put("Registered", "1");

		courseInformation.put("Information", new HashMap<String, String>());
		courseInformation.get("Information").put("Details", "This is testing material");

		courseInformation.put("RegisteredStudents", new HashMap<String, String>());
		courseInformation.put("RegisteredStudents", students);

		course.put(departmentIdentifier + "349", courseInformation);

		// Each course can have ..* course information fields
		HashMap<String, HashMap<String, String>> courseInformation2 = new HashMap<String, HashMap<String, String>>();
		// Each course can have 0..* students
		HashMap<String, String> students2 = new HashMap<String, String>();
		students2.put(departmentIdentifier + "S223", "John Doe");

		courseInformation2.put("Capacity", new HashMap<String, String>());
		courseInformation2.get("Capacity").put("TotalCapacity", "3");
		courseInformation2.get("Capacity").put("Registered", "1");

		courseInformation2.put("Information", new HashMap<String, String>());
		courseInformation2.get("Information").put("Details", "This is testing material");

		courseInformation2.put("RegisteredStudents", new HashMap<String, String>());
		courseInformation2.put("RegisteredStudents", students2);
		course.put(departmentIdentifier + "1234", courseInformation2);

		// Each course can have ..* course information fields
		HashMap<String, HashMap<String, String>> courseInformation3 = new HashMap<String, HashMap<String, String>>();
		// Each course can have 0..* students
		HashMap<String, String> students3 = new HashMap<String, String>();
		students3.put(departmentIdentifier + "S556", "John Doe");

		courseInformation3.put("Capacity", new HashMap<String, String>());
		courseInformation3.get("Capacity").put("TotalCapacity", "3");
		courseInformation3.get("Capacity").put("Registered", "1");

		courseInformation3.put("Information", new HashMap<String, String>());
		courseInformation3.get("Information").put("Details", "This is testing material");

		courseInformation3.put("RegisteredStudents", new HashMap<String, String>());
		courseInformation3.put("RegisteredStudents", students3);
		course.put(departmentIdentifier + "556", courseInformation3);

		courseRecords.put("WINTER", course);

		courseRecords.put("FALL", new HashMap<String, HashMap<String, String>>());

		courseRecords.put("SUMMER", new HashMap<String, HashMap<String, String>>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#addCourse(java.lang.String, java.lang.String)
	 */

	@Override
	public synchronized String addCourse(String courseID, String semester) {
		courseID = courseID.toUpperCase();
		semester = semester.toUpperCase();

		String message = null;
		String requestState = "Failed";

		try {

			if (courseID.contains(departmentIdentifier)) {

				String semLower = semester.toLowerCase();
				// Check that the course we want to add, is not already in the database
				if (sih.courseExistsInSemester(courseID, semester, courseRecords) == false) {

					if (semLower.equalsIgnoreCase("fall") || semLower.equalsIgnoreCase("summer")
							|| semLower.equalsIgnoreCase("winter")) {

						HashMap<String, HashMap> course = null;

						try {
							// Each term can have ..* courses, but the semester might not exist
							course = courseRecords.get(semester);
						} catch (Exception e) {
							course = new HashMap<String, HashMap>();
							courseRecords.put("FALL", new HashMap<String, HashMap>());
						}

						HashMap<String, HashMap<String, String>> courseInformation = new HashMap<String, HashMap<String, String>>();

						courseInformation.put("Capacity", new HashMap<String, String>());
						courseInformation.get("Capacity").put("TotalCapacity", "3");
						courseInformation.get("Capacity").put("Registered", "0");

						// Each course can have ..* course information fields
						courseInformation.put("Information", new HashMap<String, String>());

						// Each course can have 0..* students
						courseInformation.put("RegisteredStudents", new HashMap<String, String>());

						course.put(courseID, courseInformation);

						courseRecords.put(semester, course);

						requestState = "Successful";
						message = "A new course " + courseID + " has been added for " + semester + " semester";
					} else {
						message = "Unknown semester " + semester
								+ " was given. Approved semesters are Fall, Winter, and Summer";
					}
				} else {
					message = "A course " + courseID + " already exists for " + semester + " semester";
				}
			} else {
				message = "The courseID does not belong to the department " + departmentIdentifier;
			}
		} catch (Exception e) {
			message = "An unknown error occurred: " + e;
		}

		// message =
		s.logger("Add a course (advisor)",
				"semester = " + semester + ", courseID = " + courseID + ", semester = " + semester, requestState,
				message);

		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#addStudentCourse(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized String addStudentCourse(String studentID, String newCourseID, String oldCourse) {
		studentID = studentID.toUpperCase();
		newCourseID = newCourseID.toUpperCase();
		oldCourse = oldCourse.toUpperCase();

		String successful = "false";
		System.out.println("Before adding a student course " + newCourseID);
		if (sih.courseExists(newCourseID, courseRecords)) {
			System.out.println("First try ");
			// Looping through course
			for (Map.Entry me : courseRecords.entrySet()) {

				// Each term can have ..* courses
				HashMap<String, HashMap> course = courseRecords.get(me.getKey());

				// Looping through course
				for (Map.Entry me2 : course.entrySet()) {
					System.out.println("Second try ");
					System.out.println("Second try test " + newCourseID.contains((String) me2.getKey()));
					// Changing the new course information
					if (newCourseID.contains((String) me2.getKey())) {

						HashMap<String, String> student = (HashMap<String, String>) course.get(me2.getKey())
								.get("RegisteredStudents");

						if (student.get(studentID) == null) {

							HashMap<String, String> capacity = (HashMap<String, String>) course.get(me2.getKey())
									.get("Capacity");

							int registered = Integer.parseInt(capacity.get("Registered"));
							int totalCapacity = Integer.parseInt(capacity.get("TotalCapacity"));
							if (registered < totalCapacity) {
								student.put(studentID, "NoName");

								capacity.put("Registered", (registered + 1) + "");
								successful = "true";
							}

						} else {
							successful = "samecourse";
						}

					} // End of if (Changing the new course information)

				} // End of for loop

			} // End of for loop

		} else {
			successful = "nocourse";
		}

		return successful;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * Interfaces.ServerInterfaceOperations#listCourseAvailability(org.omg.CORBA.
	 * String)
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#listCourseAvailability(java.lang.String)
	 */
	@Override
	public synchronized String listCourseAvailability(String semester) {
		semester = semester.toUpperCase();

		String message = "";
		String requestState = "Failed";

		/*
		 * 
		 * SOEN - UDP server starts at 8081
		 * 
		 * INSE - UDP server starts at 9091
		 * 
		 * COMP - UDP server starts at 10011
		 */
		try {

			// Below could have been done using looping also. Because this application is
			// just for testing and proof-of-concept
			// purposes, the UDP protocol was used instead of TCP (more reliable) or
			// multicast for a reason.
			if (!departmentIdentifier.equalsIgnoreCase("SOEN")) {
				message += sendUDPMessage(8081, semester);
			} else {
				message += semesterCourses(semester);
			}
			if (!departmentIdentifier.equalsIgnoreCase("INSE")) {
				message += sendUDPMessage(9091, semester);
			} else {
				message += semesterCourses(semester);
			}
			if (!departmentIdentifier.equalsIgnoreCase("COMP")) {
				message += sendUDPMessage(10011, semester);
			} else {
				message += semesterCourses(semester);
			}

		} catch (Exception e) {

		}

		if (!message.contains("nullnull") && !message.contains("nullnullnull")) {
			requestState = "Successful";
			s.logger("List course availability (advisor)", "semester = " + semester, requestState, message);
		} else {
			requestState = "Failed";
			message = "No available UDP services for course retrieval";
			s.logger("List course availability (advisor)", "semester = " + semester, requestState, message);
		}

		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#removeCourse(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized String removeCourse(String courseID, String semester) {
		courseID = courseID.toUpperCase();
		semester = semester.toUpperCase();

		String message = null;
		String requestState = "Failed";

		try {
			String semLower = semester.toLowerCase();

			// Check that the course we want to add, is not already in the database
			if (sih.courseExistsInSemester(courseID, semester, courseRecords) == true) {
				if (semLower.equalsIgnoreCase("fall") || semLower.equalsIgnoreCase("summer")
						|| semLower.equalsIgnoreCase("winter")) {

					// Each term can have ..* courses
					HashMap<String, HashMap> course = courseRecords.get(semester);

					course.remove(courseID);

					courseRecords.put(semester, course);

					requestState = "Successful";
					message = "A course " + courseID + " has been deleted for " + semester
							+ " semester and the students have been dropped from the course";
				} else {
					message = "Unknown semester " + semester
							+ " was given. Approved semesters are Fall, Winter, and Summer";
				}
			} else {
				message = "A course " + courseID + " does not exists for " + semester + " semester";
			}

		} catch (Exception e) {
			message = "An unknown error occurred: " + e;
		}

		// message =
		s.logger("Delete a course (advisor)",
				"semester = " + semester + ", courseID = " + courseID + ", semester = " + semester, requestState,
				message);

		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#removeStudentCourse(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized String removeStudentCourse(String studentID, String newCourseID, String oldCourse) {
		studentID = studentID.toUpperCase();
		oldCourse = oldCourse.toUpperCase();
		newCourseID = newCourseID.toUpperCase();

		String successful = "false";

		System.out.println(" First try removeStudentCourse " + sih.courseExists(oldCourse, courseRecords) + " "
				+ sih.studentHasACourse(studentID, oldCourse, courseRecords));

		if (sih.courseExists(oldCourse, courseRecords) && sih.studentHasACourse(studentID, oldCourse, courseRecords)) {
			// Looping through course
			for (Map.Entry me : courseRecords.entrySet()) {

				// Each term can have ..* courses
				HashMap<String, HashMap> course = courseRecords.get(me.getKey());

				// Looping through course
				for (Map.Entry me2 : course.entrySet()) {

					// Changing the old course information
					if (oldCourse.contains((String) me2.getKey())) {

						HashMap<String, String> student = (HashMap<String, String>) course.get(me2.getKey())
								.get("RegisteredStudents");

						if (student.get(studentID) != null) {

							student.remove(studentID);

							HashMap<String, String> capacity = (HashMap<String, String>) course.get(me2.getKey())
									.get("Capacity");

							int registered = Integer.parseInt(capacity.get("Registered"));

							capacity.put("Registered", (registered - 1) + "");
							successful = "true";

						} else { // End of if

						}
					} // End of if (Changing the old course information)

				} // End of for loop

			} // End of for loop

		} else {
			successful = "nocourse";
		}

		return successful;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#dropCourse(java.lang.String, java.lang.String)
	 */

	@Override
	public synchronized String dropCourse(String studentID, String courseID) {
		studentID = studentID.toUpperCase();
		courseID = courseID.toUpperCase();

		String message = null;
		String requestState = "Failed";
		try {
			// Looping through all of the semesters
			for (Map.Entry me : courseRecords.entrySet()) {
				// Looping through course records
				HashMap<String, HashMap> semesterRow = courseRecords.get(me.getKey());

				// We need to loop each semester, because a course can exist in multiple
				// semesters. The given assignment did not specify the semester id for a method
				// so this is necessary.
				for (Entry<String, HashMap> me2 : semesterRow.entrySet()) {

					HashMap<String, String> courseStudentsRow = (HashMap<String, String>) semesterRow.get(me2.getKey())
							.get("RegisteredStudents");

					// If the studentID exists for the course.
					try {

						if (courseStudentsRow.get(studentID) != null
								&& me2.getKey().toString().equalsIgnoreCase(courseID)) {

							HashMap<String, String> capacity = (HashMap<String, String>) semesterRow.get(me2.getKey())
									.get("Capacity");

							int registered = Integer.parseInt(capacity.get("Registered"));

							capacity.put("Registered", (registered - 1) + "");

							courseStudentsRow.remove(studentID);

							// semesterRow.get(me2.getKey()).put("RegisteredStudents", courseStudentsRow);

							// courseRecords.put((String) me.getKey(), semesterRow); //
							// semesterRow.get(me2.getKey())

							message = "The student with ID " + studentID + " has been dropped from the course "

									+ courseID;

							requestState = "Successful";
						} /*
							 * else { message = "The student with ID " + studentID +
							 * " did not have a course " + courseID + " that could be dropped."; }
							 */
					} catch (NullPointerException e) {

						message = "The student with ID " + studentID + " did not have a course " + courseID
								+ " that could be dropped.";
					}
				}
			}
		} catch (Exception e) {
			requestState = "Failed";
			message = "An unknown error occurred: " + e;
		}

		if (message == null) {
			message = "The student with ID " + studentID + " did not have a course " + courseID
					+ " that could be dropped.";
		}

		System.out.println("Result after dropping " + courseRecords);

		s.logger("Drop a course (student)", "studentID = " + studentID + ", courseID = " + courseID, requestState,
				message);

		return message;
	}

	// We assume that the client sends the request to the right server i.e. the
	// client sends a request for the course department, but we will still check
	// whether or not the student really belongs to this servers department.
	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#enrolCourse(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public synchronized String enrolCourse(String studentID, String courseID, String semester) {
		studentID = studentID.toUpperCase();
		courseID = courseID.toUpperCase();
		semester = semester.toUpperCase();

		String message = null;
		String requestState = "Failed";

		System.out.println("Trying to enrol the student");

		try {

			// Check if the semester matches any known semester in the database
			if (courseRecords.get(semester) != null) {

				// Check if the semester course matches any semester course in the database
				if (courseRecords.get(semester).get(courseID) != null) {

					// Check if the course has any room by iterating through the database
					HashMap<String, HashMap<String, String>> courseInformation = (HashMap<String, HashMap<String, String>>) courseRecords
							.get(semester).get(courseID);

					int totalCapacity = Integer.parseInt(courseInformation.get("Capacity").get("TotalCapacity"));
					int registered = Integer.parseInt(courseInformation.get("Capacity").get("Registered"));

					// If the course still has some room.
					if (registered < totalCapacity) {

						// We need to make sure that the student has taken max 3 courses per semester if
						// part of the department and max 2 courses from this department if the student
						// is from another department. There is a small problem though. This method does
						// not check whether or not the student has been already registered for this
						// course, but it is not a problem since it will overwrite the hashmap value.
						// It would be better of course if we would return a proper error message.
						if (studentSemesterCourses(studentID, semester, courseID) == true) {

							if (courseInformation.get("RegisteredStudents").get(studentID) == null) {
								registered += 1;

								// Each term can have ..* courses
								HashMap<String, HashMap> course = new HashMap<String, HashMap>();

								// Each course can have 0..* students
								HashMap<String, String> students = courseInformation.get("RegisteredStudents");
								students.put(studentID, "NoName");

								courseInformation.get("Capacity").put("Registered", registered + "");

								courseInformation.put("RegisteredStudents", new HashMap<String, String>());
								courseInformation.put("RegisteredStudents", students);

								course.put(courseID, courseInformation);

								courseRecords.get(semester).replace(courseID, course.get(courseID));

								requestState = "Successful";
								message = "The student " + studentID + " is registered for the course " + courseID;
							} else {
								message = "The student " + studentID + " is already registered for the course "
										+ courseID;
							}

						} else {
							message = "The student cannot register for more courses in the given semester";
						}

					} else {
						message = "The course " + courseID + " has no room left";
					}

				} else {
					message = "Course  " + courseID + "  not found in the database";
				}
			} else {
				message = "Semester not found in the database";
			}

		} catch (Exception e) {
			message = "An unknown error occurred: " + e;

		}

		System.out.println(courseRecords);

		s.logger("Enrol to a course (student)",
				"studentID = " + studentID + ", courseID = " + courseID + ", semester = " + semester, requestState,
				message);

		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#getClassSchedule(java.lang.String)
	 */
	@Override
	public synchronized String getClassSchedule(String studentID) {
		studentID = studentID.toUpperCase();

		String message = departmentIdentifier + " student courses = ";
		String requestState = "Successful";

		try {
			// Looping through all of the semesters
			for (Map.Entry me : courseRecords.entrySet()) {

				HashMap<String, HashMap> semesterRow = courseRecords.get(me.getKey());

				for (Entry<String, HashMap> me2 : semesterRow.entrySet()) {
					HashMap<String, String> courseStudentsRow = (HashMap<String, String>) semesterRow.get(me2.getKey())
							.get("RegisteredStudents");

					// if (courseStudentsRow.get(studentID) != null) {
					if (courseStudentsRow.toString().contains(studentID)) {
						message += "semester is " + me.getKey() + " and course is " + me2.getKey() + ", ";
					}

				}

			}
		} catch (Exception e) {
			requestState = "Failed";
			message = "An unknown error occurred: " + e;
		}

		if (message.equalsIgnoreCase(departmentIdentifier + " student courses = ")) {
			message += "Student has not enrolled to any course in any semester in the department "
					+ departmentIdentifier;
		}

		s.logger("Get class schedule (student)", "studentID = " + studentID, requestState, message);

		return message;
	}

	// This method returns boolean. We need to make sure that the student has taken
	// max 3 courses per semester if part of the department and max 2 courses from
	// this department if the student is from another department
	protected synchronized boolean studentSemesterCourses(String studentID, String semester, String courseID)
			throws MalformedURLException, NumberFormatException, RemoteException {
		studentID = studentID.toUpperCase();
		courseID = courseID.toUpperCase();
		semester = semester.toUpperCase();

		URL wsdlURL1 = new URL("http://localhost:8080/ws/server");
		URL wsdlURL2 = new URL("http://localhost:9090/ws/server");
		URL wsdlURL3 = new URL("http://localhost:10010/ws/server");

		QName qname1 = new QName("http://SOEN/", "ServerImplService");
		QName qname2 = new QName("http://INSE/", "ServerImplService");
		QName qname3 = new QName("http://COMP/", "ServerImplService");

		Service service1 = Service.create(wsdlURL1, qname1);
		Service service2 = Service.create(wsdlURL2, qname2);
		Service service3 = Service.create(wsdlURL3, qname3);

		ServerInterface soen = service1.getPort(ServerInterface.class);
		ServerInterface inse = service2.getPort(ServerInterface.class);
		ServerInterface comp = service3.getPort(ServerInterface.class);
		int thisDepartmentCounter = 0;

		int otherDepartmentCounter = 0;

		int ownDepartmentCounter = 0;

		String studentID2 = new String(studentID);
		String semester2 = new String(semester);

		// Own department semester courses
		if (!studentID.contains(departmentIdentifier)) {
			thisDepartmentCounter += Integer.parseInt(studentSemesterCourseCount(studentID2, semester2));
		} else {
			ownDepartmentCounter += Integer.parseInt(studentSemesterCourseCount(studentID2, semester2));
		}

		// Other department semester courses.
		// !departmentIdentifier.equalsIgnoreCase("INSE") means that we don't want to
		// send a CORBA request back to the same server which requested the data. See
		// above.
		if (!departmentIdentifier.equalsIgnoreCase("INSE") && !studentID.contains("INSE")) {
			otherDepartmentCounter += Integer.parseInt(inse.studentSemesterCourseCount(studentID2, semester2));
		} else if (!departmentIdentifier.equalsIgnoreCase("INSE") && studentID.contains("INSE")) {
			// If the studentID correlates with a department server and that server is not
			// this one, then...
			ownDepartmentCounter += Integer.parseInt(inse.studentSemesterCourseCount(studentID2, semester2));
		}

		if (!departmentIdentifier.equalsIgnoreCase("SOEN") && !studentID.contains("SOEN")) {
			otherDepartmentCounter += Integer.parseInt(soen.studentSemesterCourseCount(studentID2, semester2));
		} else if (!departmentIdentifier.equalsIgnoreCase("SOEN") && studentID.contains("SOEN")) {
			// If the studentID correlates with a department server and that server is not
			// this one, then...
			ownDepartmentCounter += Integer.parseInt(soen.studentSemesterCourseCount(studentID2, semester2));
		}

		if (!departmentIdentifier.equalsIgnoreCase("COMP") && !studentID.contains("COMP")) {
			otherDepartmentCounter += Integer.parseInt(comp.studentSemesterCourseCount(studentID2, semester2));
		} else if (!departmentIdentifier.equalsIgnoreCase("COMP") && studentID.contains("COMP")) {
			// If the studentID correlates with a department server and that server is not
			// this one, then...
			ownDepartmentCounter += Integer.parseInt(comp.studentSemesterCourseCount(studentID2, semester2));
		}

		// A student can only take 3 course per semester if he/she is part of the
		// department, but if the student is not part of the department, then the
		// student can only take 2 courses per semester. Total number of courses
		// that can be taken in a semester is 3.

		// If you are part of this department (for example INSE) and total semester
		// course count is 3 or less than 3
		System.out.println("Departmentcounter " + thisDepartmentCounter + " " + otherDepartmentCounter);
		System.out.println("Logic : " + thisDepartmentCounter + " " + otherDepartmentCounter + " "
				+ ownDepartmentCounter + " " + studentID.contains(courseID) + " " + studentID + " " + courseID);

		// Own department can be less than 3
		// Other departments can be less than 2
		if ((ownDepartmentCounter + thisDepartmentCounter + otherDepartmentCounter) < 3) {
			if ((thisDepartmentCounter + otherDepartmentCounter) < 2 && !studentID.contains(departmentIdentifier)) { // courseID
				return true;
			} else if ((thisDepartmentCounter + otherDepartmentCounter) <= 2
					&& studentID.contains(departmentIdentifier)) { // courseID
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#swapCourse(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public synchronized String swapCourse(String studentID, String newCourseID, String oldCourse) {
		studentID = studentID.toUpperCase();
		newCourseID = newCourseID.toUpperCase();
		oldCourse = oldCourse.toUpperCase();

		String message = "";

		String sentMessage = "";

		String newCourseDepartment = "";
		String oldCourseDepartment = "";

		if (newCourseID.contains("SOEN")) {
			newCourseDepartment = "SOEN";
		} else if (newCourseID.contains("INSE")) {
			newCourseDepartment = "INSE";
		} else if (newCourseID.contains("COMP")) {
			newCourseDepartment = "COMP";
		}

		if (oldCourse.contains("SOEN")) {
			oldCourseDepartment = "SOEN";
		} else if (oldCourse.contains("INSE")) {
			oldCourseDepartment = "INSE";
		} else if (oldCourse.contains("COMP")) {
			oldCourseDepartment = "COMP";
		}

		String message1 = "";
		String message2 = "";

		System.out.println("Departments " + newCourseID.contains(departmentIdentifier) + " "
				+ oldCourse.contains(departmentIdentifier));

		// revertRemove, revertAdd, removeStudentCourseUDP, addStudentCourseUDP
		// The below method is another way to do the logic. It needs to be refined to
		// handle all kinds
		// of request types and exception cases
		if (newCourseID.contains(departmentIdentifier) && oldCourse.contains(departmentIdentifier)) {
			message = swapCourseOwnDepartment(studentID, newCourseID, oldCourse);
		}

		if (newCourseID.contains(departmentIdentifier)) {
			message1 = "" + addStudentCourse(studentID, newCourseID, oldCourse);
		} else {
			// 0 = switch method (methodName), 1 = studentID, 2 = newCourseID, 3 =
			// oldCourseID
			sentMessage = "addStudentCourse " + studentID + " " + newCourseID + " " + oldCourse;
			message1 = sendUDPMessage(departmentToPort.get(newCourseDepartment), sentMessage);
		}

		System.out
				.println("if (oldCourse.contains(departmentIdentifier)) { " + oldCourse.contains(departmentIdentifier));

		if (oldCourse.contains(departmentIdentifier)) {
			message2 = "" + removeStudentCourse(studentID, newCourseID, oldCourse);
		} else {
			sentMessage = "removeStudentCourse " + studentID + " " + newCourseID + " " + oldCourse;
			message2 = sendUDPMessage(departmentToPort.get(oldCourseDepartment), sentMessage);
		}

		System.out.println("Message 1 " + message1 + " " + message2);

		if (message1.contains("true") && message2.contains("true")) {
			message = "The student course swap was successfully executed with studentID " + studentID + ", newCourseID "
					+ newCourseID + ", and oldCourseID " + oldCourse;
		} else {

			/*
			 * 
			 * ------------------------------
			 * 
			 * Please provide the new courseID for the given service (try testing SOEN1234):
			 * COMP1234 (Currently registered)
			 * 
			 * Please provide the old courseID for the given service (try testing SOEN1234):
			 * SOEN1234 (Not registered)
			 * 
			 * ------------------------------
			 * 
			 * Please provide the new courseID for the given service (try testing SOEN1234):
			 * SOEN1234 (NOT registered)
			 * 
			 * Please provide the old courseID for the given service (try testing SOEN1234):
			 * SOEN1234 (Not registered)
			 * 
			 * ------------------------------
			 * 
			 */

			System.out.println("\n\n");
			System.out.println("BEFORE SENDING AN ERROR REQUEST FOR REVERTS");
			System.out.println(newCourseID + " " + oldCourse + " " + studentID);

			/*
			 * if (!message2.contains("nocourse") && !message1.contains("samecourse")) { if
			 * (newCourseID.contains(departmentIdentifier)) {
			 * 
			 * revertAdd(studentID, newCourseID); } else if
			 * (!newCourseID.contains(departmentIdentifier)) {
			 * 
			 * boolean success = false; int counter = 0;
			 * 
			 * while (!success && counter <= 3) {
			 * 
			 * sentMessage = "revertAdd " + studentID + " " + newCourseID; message2 =
			 * sendUDPMessage(departmentToPort.get(newCourseDepartment), sentMessage); if
			 * (message2.contains("true")) { success = true; } try {
			 * TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } counter++; } }
			 * 
			 * if (oldCourse.contains(departmentIdentifier)) {
			 * 
			 * revertRemove(studentID, newCourseID); } else if
			 * (!oldCourse.contains(departmentIdentifier)) {
			 * 
			 * boolean success = false;
			 * 
			 * int counter = 0;
			 * 
			 * while (!success && counter <= 3) {
			 * 
			 * sentMessage = "revertRemove " + studentID + " " + oldCourse; message2 =
			 * sendUDPMessage(departmentToPort.get(oldCourseDepartment), sentMessage); if
			 * (message2.contains("true")) { success = true; } try {
			 * TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) {
			 * e.printStackTrace(); } counter++; }
			 * 
			 * } }
			 */
			if (message1.contains("nocourse") && message2.contains("true")) {

				if (oldCourse.contains(departmentIdentifier)) {
					revertAdd(studentID, oldCourse);
				} else if (!oldCourse.contains(departmentIdentifier)) {
					boolean success = false;
					int counter = 0;

					while (!success && counter <= 3) {

						sentMessage = "revertAdd " + studentID + " " + oldCourse;
						message2 = sendUDPMessage(departmentToPort.get(oldCourseDepartment), sentMessage);
						if (message2.contains("true")) {
							success = true;
						}
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						counter++;
					}
				}

			} else {

				/*
				 * Please provide the new courseID for the given service (try testing SOEN1234):
				 * SOEN349 Please provide the old courseID for the given service (try testing
				 * SOEN1234): INSE1234
				 */

				// Message 1 samecourse true
				System.out.println("Breaking here " + message1 + " " + message2);
				// If the student is not trying to register for already registered course, but
				// the old course does not exist for the student
				if (!message1.contains("samecourse") && !message2.contains("nocourse") && !message2.contains("true")) {

					System.out.println("NO");
					if (newCourseID.contains(departmentIdentifier)) {
						revertRemove(studentID, newCourseID);
					} else if (!newCourseID.contains(departmentIdentifier)) {

						boolean success = false;

						while (!success) {
							sentMessage = "revertRemove " + studentID + " " + newCourseID;
							message1 = sendUDPMessage(departmentToPort.get(newCourseDepartment), sentMessage);
							if (message1.contains("true")) {
								success = true;
							}
							try {
								TimeUnit.SECONDS.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				if (message1.contains("samecourse") && message2.contains("nocourse")) {

					// Don't do anything

				} else {
					System.out.println("YES");

					// message1 = true (adding a new course was ok), message2= nocourse (student did
					// not have a course)

					// if (!message1.contains("samecourse") && !message2.contains("true")) {
					if (message1.contains("true") && message2.contains("nocourse")
							|| message1.contains("nocourse") && message2.contains("nocourse")) {
						if (newCourseID.contains(departmentIdentifier)) {
							revertRemove(studentID, newCourseID);
						} else if (!newCourseID.contains(departmentIdentifier)) {
							sentMessage = "revertRemove " + studentID + " " + newCourseID;
							message2 = sendUDPMessage(departmentToPort.get(newCourseDepartment), sentMessage);
						}

					} else {
						// If the student is trying to register for already registered course, but
						// the old course does not exist for the student
						if (oldCourse.contains(departmentIdentifier)) {

							revertAdd(studentID, oldCourse);
						} else if (!oldCourse.contains(departmentIdentifier)) {

							boolean success = false;
							int counter = 0;

							while (!success && counter <= 3) {

								sentMessage = "revertAdd " + studentID + " " + oldCourse;
								message2 = sendUDPMessage(departmentToPort.get(oldCourseDepartment), sentMessage);
								if (message2.contains("true")) {
									success = true;
								}
								try {
									TimeUnit.SECONDS.sleep(1);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								counter++;
							}
						}
					}

				}

			}

			System.out.println("At the end " + message1 + " " + message2);

			message = "The student course swap was NOT A SUCCESS with studentID " + studentID + ", newCourseID "
					+ newCourseID + ", and oldCourseID " + oldCourse;
		}

		return message;

	}

	private synchronized String swapCourseOwnDepartment(String studentID, String newCourseID, String oldCourse) {
		studentID = studentID.toUpperCase();
		newCourseID = newCourseID.toUpperCase();
		oldCourse = oldCourse.toUpperCase();

		String message = "No courses found with the identifying information such as studentID " + studentID
				+ ", newCourseID " + newCourseID + ", and oldCourseID " + oldCourse;

		// If the old course ID is the same as the looped course and if new course
		// exists so that the swap is atomic
		if (sih.courseExists(oldCourse, courseRecords) && sih.courseExists(newCourseID, courseRecords)) {

			// Looping through course
			for (Map.Entry me : courseRecords.entrySet()) {

				// Each term can have ..* courses
				HashMap<String, HashMap> course = courseRecords.get(me.getKey());

				// Looping through course
				for (Map.Entry me2 : course.entrySet()) {

					// Changing the old course information
					if (oldCourse.equalsIgnoreCase((String) me2.getKey())) {

						HashMap<String, String> student = (HashMap<String, String>) course.get(me2.getKey())
								.get("RegisteredStudents");

						if (student.get(studentID) != null) {

							student.remove(studentID);

							HashMap<String, String> capacity = (HashMap<String, String>) course.get(me2.getKey())
									.get("Capacity");

							int registered = Integer.parseInt(capacity.get("Registered"));

							capacity.put("Registered", (registered - 1) + "");

						} // End of if
					} // End of if (Changing the old course information)

					// Changing the new course information
					if (newCourseID.equalsIgnoreCase((String) me2.getKey())) {

						HashMap<String, String> student = (HashMap<String, String>) course.get(me2.getKey())
								.get("RegisteredStudents");

						if (student.get(studentID) == null) {

							HashMap<String, String> capacity = (HashMap<String, String>) course.get(me2.getKey())
									.get("Capacity");

							int registered = Integer.parseInt(capacity.get("Registered"));
							int totalCapacity = Integer.parseInt(capacity.get("TotalCapacity"));
							if (registered < totalCapacity) {
								student.put(studentID, "NoName");

								capacity.put("Registered", (registered + 1) + "");

								message = "The student with studentID " + studentID
										+ " was successfully registered for a new course newCourseID " + newCourseID
										+ " and removed from old course oldCourseID " + oldCourse;
							} else {
								message = departmentIdentifier
										+ " department doesnt have enough room in the new course for adding a student with studentID "
										+ studentID;
							}

						}

					} // End of if (Changing the new course information)

				} // End of for loop

			} // End of for loop
		} else {
			message = "The given new or old course does not exist in the database";
		}
		return message;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#revertAdd(java.lang.String, java.lang.String)
	 */

	@Override
	public synchronized boolean revertAdd(String studentID, String courseID) {
		studentID = studentID.toUpperCase();
		courseID = courseID.toUpperCase();

		System.out.println("Trying to revert add");
		boolean successful = false;

		// Looping through course
		for (Map.Entry me : courseRecords.entrySet()) {

			// Each term can have ..* courses
			HashMap<String, HashMap> course = courseRecords.get(me.getKey());

			// Looping through course
			for (Map.Entry me2 : course.entrySet()) {

				// Changing the old course information
				if (courseID.contains((String) me2.getKey())) {
					// Revert the action by putting the student back into the hashmap

					HashMap<String, String> student = (HashMap<String, String>) course.get(me2.getKey())
							.get("RegisteredStudents");

					student.put(studentID, "NoName");

					HashMap<String, String> capacity = (HashMap<String, String>) course.get(me2.getKey())
							.get("Capacity");

					int registered = Integer.parseInt(capacity.get("Registered"));

					capacity.put("Registered", (registered + 1) + "");
					successful = true;

				} // End of if (Changing the old course information)

			} // End of for loop
		}
		System.out.println("Trying to revert add end " + successful);
		return successful;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#revertRemove(java.lang.String, java.lang.String)
	 */
	@Override
	public synchronized boolean revertRemove(String studentID, String courseID) {
		studentID = studentID.toUpperCase();
		courseID = courseID.toUpperCase();

		boolean successful = false;

		// Looping through course
		for (Map.Entry me : courseRecords.entrySet()) {

			// Each term can have ..* courses
			HashMap<String, HashMap> course = courseRecords.get(me.getKey());

			// Looping through course
			for (Map.Entry me2 : course.entrySet()) {

				// Changing the old course information
				if (courseID.contains((String) me2.getKey())) {

					HashMap<String, String> student = (HashMap<String, String>) course.get(me2.getKey())
							.get("RegisteredStudents");

					if (student.get(studentID) != null) {

						student.remove(studentID);

						HashMap<String, String> capacity = (HashMap<String, String>) course.get(me2.getKey())
								.get("Capacity");

						int registered = Integer.parseInt(capacity.get("Registered"));

						capacity.put("Registered", (registered - 1) + "");

						successful = true;
					} // End of if

				} // End of if (Changing the old course information)

			} // End of for loop

		}

		return successful;
	}

	// The below method returns all of the courses for a given semester
	protected synchronized String semesterCourses(String semester) {
		semester = semester.toUpperCase();

		// Fall - COMP6231 5, SOEN6441 4, SOEN6497 0, INSE6132 5
		String message = null;
		try {
			// Each term can have ..* courses
			HashMap<String, HashMap> semesterCourses = courseRecords.get(semester);
			message = departmentIdentifier + " " + semester + " - ";
			for (Map.Entry me : semesterCourses.entrySet()) {
				HashMap<String, String> capacity = (HashMap<String, String>) semesterCourses.get(me.getKey())
						.get("Capacity");
				int totalCapacity = Integer.parseInt(capacity.get("TotalCapacity"));
				int registered = Integer.parseInt(capacity.get("Registered"));
				message += me.getKey() + " " + (totalCapacity - registered) + ", ";
			}

			if (message.equalsIgnoreCase(departmentIdentifier + " " + semester + " - ")) {
				message += "No courses available for the given semester, ";
			}

		} catch (Exception e) {
			throw e;
		}
		return message;
	}

	protected synchronized String sendUDPMessage(int serverPort, String messagePassed) {
		DatagramSocket aSocket = null;

		try {
			aSocket = new DatagramSocket(null);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		String courses = "";
		try {

			aSocket.setSoTimeout(20000);

			courses = "No courses for semester available";

			byte[] message = messagePassed.getBytes();

			InetAddress aHost = InetAddress.getByName("localhost");

			DatagramPacket request = new DatagramPacket(message, messagePassed.length(), aHost, serverPort);

			aSocket.send(request);

			byte[] buffer = new byte[1000];

			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

			aSocket.receive(reply);

			// I want to get the data again in another byte array so that I will actually
			// send the right String value after receiving it.
			byte[] bufferCopy = new byte[reply.getLength()];

			// Copies an array from the specified source array, beginning at the specified
			// position, to the specified position of the destination array.
			System.arraycopy(reply.getData(), reply.getOffset(), bufferCopy, 0, reply.getLength());

			// Creating a new string for the copy
			String bufferData = new String(bufferCopy);

			courses = bufferData;

		} catch (SocketTimeoutException e) {
			courses = "Timeout occurred for port " + serverPort;
		} catch (SocketException e) {
			return null;
			// We don't want to return a detailed exception
			// System.out.println("Socket error: " + e.getMessage());
			// return "Socket error: " + e.getMessage();
		} catch (IOException e) {
			return null;
			// System.out.println("IO error: " + e.getMessage());
			// return "IO error: " + e.getMessage();
		} finally {
			if (aSocket != null)
				aSocket.close();
		}

		return courses;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see SOEN.ServerInterface#studentSemesterCourseCount(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public synchronized String studentSemesterCourseCount(String studentID, String semester) {
		studentID = studentID.toUpperCase();
		semester = semester.toUpperCase();

		int counter = 0;

		// Each term can have ..* courses
		HashMap<String, HashMap> course = courseRecords.get(semester);

		// Looping through course
		for (Map.Entry me : course.entrySet()) {

			HashMap<String, HashMap> student = (HashMap<String, HashMap>) course.get(me.getKey())
					.get("RegisteredStudents");

			// Student has taken the course
			if (student.get(studentID) != null) {
				counter += 1;
			}

		}
		return counter + "";
	}

}
