package INSE;

import java.util.HashMap;
import java.util.Map;

public class ServerImplHelper {

	// This method checks if the student belongs to the department
	protected synchronized boolean studentBelongsToDepartment(String studentID, String departmentIdentifier) {
		boolean belongsToDepartment = false;
		// Check if the student belongs to this department
		if (studentID.toLowerCase().contains(departmentIdentifier.toLowerCase())) {
			belongsToDepartment = true;
			// System.out.println("Student " + studentID + " is part of the department");
		} else {
			belongsToDepartment = false;
			// message = "Student " + studentID + " is NOT part of the department";
		}
		return belongsToDepartment;
	}

	public synchronized boolean courseExists(String courseID, HashMap<String, HashMap> courseRecords) {

		boolean newCourseExists = false;
		System.out.println("courseExists " + courseID);
		// We need to first check that the new course really exists
		// Looping through course
		for (Map.Entry me : courseRecords.entrySet()) {

			// Each term can have ..* courses
			HashMap<String, HashMap> course = courseRecords.get(me.getKey());

			// Looping through course
			for (Map.Entry me2 : course.entrySet()) {

				System.out.println(courseID.contains((String) me2.getKey()) + " Looping through semester " + me.getKey()
						+ " and " + me2.getKey() + " " + courseID);
				if (courseID.contains((String) me2.getKey())) {
					newCourseExists = true;
					System.out.println("The new course exists");
				}

			}

		}
		return newCourseExists;

	}

	public synchronized boolean studentHasACourse(String studentID, String courseID,
			HashMap<String, HashMap> courseRecords) {
		boolean isStudentCourse = false;
		for (Map.Entry me : courseRecords.entrySet()) {

			// Each term can have ..* courses
			HashMap<String, HashMap> course = courseRecords.get(me.getKey());
			// Looping through course
			for (Map.Entry me2 : course.entrySet()) {

				HashMap<String, HashMap> student = (HashMap<String, HashMap>) course.get(me2.getKey())
						.get("RegisteredStudents");

				System.out.println("Checking if the student has a course " + student.get(studentID) + " "
						+ courseID.contains((CharSequence) me2.getKey()));

				System.out.println("Checking if the student has a course 2 " + student.get(studentID) + " "
						+ courseID.equalsIgnoreCase((String) me2.getKey()));

				System.out.println("Testing " + courseID + " " + me2.getKey());

				// Student has taken the course
				if (student.get(studentID) != null && courseID.contains((CharSequence) me2.getKey())) {
					isStudentCourse = true;
				}

			}

		}
		return isStudentCourse;

	}

	// The below method checks if a course exists in the given semester
	protected synchronized boolean courseExistsInSemester(String courseID, String semester,
			HashMap<String, HashMap> courseRecords) {
		boolean exists = false;
		try {
			// Each term can have ..* courses
			HashMap<String, HashMap> course = courseRecords.get(semester);

			if (course.get(courseID) != null) {
				exists = true;
			}
		} catch (Exception e) {
			exists = false;
		}
		return exists;
	}

}
