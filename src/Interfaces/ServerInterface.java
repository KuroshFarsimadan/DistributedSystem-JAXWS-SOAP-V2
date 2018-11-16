package Interfaces;

import java.rmi.Remote;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/*
 * Simple rules and theory:
 * - Java API for developing web services and clients over SOAP is called JAX-RPC (JAX-WS is successor) 
 * - In this application, we are using a mix of JAX-WS and JAX-RPC.
 * - JAX-RPC maps some of the types in the java language definitions in XML used in both SOAP messages
 * and service descriptions
 * - Rules for JAX-RPC
 * 		- It must extend Remote interface
 * 		- It must not have constant declarations
 * 		- The methods must throw java.rmi.RemoteException or one of its subclasses
 * 		- Method parameters and return types must be permitted JAX-RPC types
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface ServerInterface extends Remote {

	@WebMethod
	String addCourse(String courseID, String semester) throws java.rmi.RemoteException;

	@WebMethod
	String addStudentCourse(String studentID, String newCourseID, String oldCourse) throws java.rmi.RemoteException;

	@WebMethod
	String listCourseAvailability(String semester) throws java.rmi.RemoteException;

	@WebMethod
	String removeCourse(String courseID, String semester) throws java.rmi.RemoteException;

	@WebMethod
	String removeStudentCourse(String studentID, String newCourseID, String oldCourse) throws java.rmi.RemoteException;

	@WebMethod
	String dropCourse(String studentID, String courseID) throws java.rmi.RemoteException;

	@WebMethod
	String enrolCourse(String studentID, String courseID, String semester) throws java.rmi.RemoteException;

	@WebMethod
	String getClassSchedule(String studentID) throws java.rmi.RemoteException;

	@WebMethod
	String swapCourse(String studentID, String newCourseID, String oldCourse) throws java.rmi.RemoteException;

	@WebMethod
	boolean revertAdd(String studentID, String courseID) throws java.rmi.RemoteException;

	@WebMethod
	boolean revertRemove(String studentID, String courseID) throws java.rmi.RemoteException;

	@WebMethod
	String studentSemesterCourseCount(String studentID, String semester) throws java.rmi.RemoteException;

}