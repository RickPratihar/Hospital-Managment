package com.hospital;
import java.sql.*;
import java.util.Scanner;

// Class representing the main Hospital Management System
public class HospitalManagementSystem {
    // Database connection details
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "rick@9382";
    
    public static void main(String[] args) {
        try{
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        // Create a scanner object for user input
        Scanner scanner = new Scanner(System.in);
        try{
            // Establish database connection
            Connection connection = DriverManager.getConnection(url, username, password);
            // Create Patient and Doctor objects for database operations
            Patient patient = new Patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            // Main menu loop print in console 
            while(true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. View Doctor Appointments");
                System.out.println("6. Cancel Appointment");
                System.out.println("7. Update Patient Details");
                System.out.println("8. Search Patient");
                System.out.println("9. Search Doctor");
                System.out.println("10. View Appointments By Patient Id");
                System.out.println("11. Exit");
                System.out.println("Enter your choice: ");
                int choice = scanner.nextInt(); 
                switch(choice){
                    case 1:
                        // Add Patient
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // View Patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        // View Doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        // Book Appointment
                        bookAppointment(patient, doctor, connection, scanner);
                        System.out.println();
                        break;
                    case 5:
                        // View Appointments for a Doctor
                    	 viewAppointmentsWithDoctors(doctor);
                        System.out.println();
                        break;
                        
                    case 6:
                        // Cancel Appointment
                        cancelAppointment(connection, scanner);
                        System.out.println();
                        break;
                    case 7:
                        // Update Patient Details
                        patient.updatePatientDetails();
                        System.out.println();
                        break;
                    case 8:
                        // Search for Patient by Name
                        searchPatientByName(patient, scanner);
                        System.out.println();
                        break;
                    case 9:
                        // Search for Doctor by Name
                        searchDoctorByName(doctor, scanner);
                        System.out.println();
                        break;
                        
                    case 10:
                        // View Appointments by Patient
                        viewAppointmentsByPatient(patient);
                        System.out.println();
                        break;
                    case 11:
                        // Exit the program
                        System.out.println("THANK YOU! FOR USING HOSPITAL MANAGEMENT SYSTEM!!");
                        return;
                    default:
                        System.out.println("Enter valid choice!!!");
                        break;
                }
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Method to book an appointment for a patient with a doctor
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner){
        System.out.print("Enter Patient Id: ");
        int patientId = scanner.nextInt();
        System.out.print("Enter Doctor Id: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        // Check if patient and doctor exist in the database
        if(patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)){
            // Check if the doctor is available on the specified date
            if(checkDoctorAvailability(doctorId, appointmentDate, connection)){
                String appointmentQuery = "INSERT INTO appointments(patient_id, doctor_id, appointment_date) VALUES(?, ?, ?)";
                try {
                    // Prepare SQL statement to insert the appointment into the database
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appointmentDate);
                    // Execute the SQL statement and check the number of affected rows
                    int rowsAffected = preparedStatement.executeUpdate();
                    if(rowsAffected > 0){
                        System.out.println("Appointment Booked!");
                    }else{
                        System.out.println("Failed to Book Appointment!");
                    }
                }catch (SQLException e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("Doctor not available on this date!!");
            }
        }else{
            System.out.println("Either doctor or patient doesn't exist!!!");
        }
    }
    
    // Method to check if a doctor is available on a specified date
    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection){
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try{
            // Prepare SQL statement to count appointments for a doctor on a specific date
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Check if any appointments exist for the doctor on the specified date
            if(resultSet.next()){
                int count = resultSet.getInt(1);
                if(count == 0){
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
     
 // Method to cancel an appointment
    public static void cancelAppointment(Connection connection, Scanner scanner) {
        System.out.print("Enter Appointment Id: ");
        int appointmentId = scanner.nextInt();
        String query = "DELETE FROM appointments WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointmentId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Appointment Cancelled Successfully!");
            } else {
                System.out.println("Failed to Cancel Appointment!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
  //call all method
 // Method to view appointments with doctor names
    public static void viewAppointmentsWithDoctors(Doctor doctor) {
        doctor.viewAppointmentsWithDoctors();
    }
    
 // Method to search for a patient by name
    public static void searchPatientByName(Patient patient, Scanner scanner) {
        System.out.print("Enter Patient Name to search: ");
        String name = scanner.next();
        patient.searchPatientByName(name);
    }
    
 // Method to search for a doctor by name
    public static void searchDoctorByName(Doctor doctor, Scanner scanner) {
        System.out.print("Enter Doctor Name to search: ");
        String name = scanner.next();
        doctor.searchDoctorByName(name);
    }
    
 // Method to view appointments for a specific patient
    public static void viewAppointmentsByPatient(Patient patient) {
        patient.viewAppointmentsByPatient();
    }

}


