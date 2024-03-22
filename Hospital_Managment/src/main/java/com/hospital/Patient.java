package com.hospital;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

// Class representing a Patient entity and handling database operations related to patients
public class Patient {
    private Connection connection; // Connection object for database interaction
    private Scanner scanner; // Scanner object for user input
    
    // Constructor to initialize the Patient object with a database connection and scanner
    public Patient(Connection connection, Scanner scanner){
        this.connection = connection;
        this.scanner = scanner;
    }
    
    // Method to add a new patient to the database
    public void addPatient(){
        // Prompt user for patient details
        System.out.print("Enter Patient Name: ");
        String name = scanner.next();
        System.out.print("Enter Patient Age: ");
        int age = scanner.nextInt();
        System.out.print("Enter Patient Gender: ");
        String gender = scanner.next();

        try{
            // Prepare SQL query to insert patient data into the database
            String query = "INSERT INTO patients(name, age, gender) VALUES(?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // Set parameters for the prepared statement
            preparedStatement.setString(1, name);
            preparedStatement.setInt(2, age);
            preparedStatement.setString(3, gender);
            // Execute the SQL statement and get the number of affected rows
            int affectedRows = preparedStatement.executeUpdate();
            // Check if the operation was successful and provide appropriate feedback
            if(affectedRows > 0){
                System.out.println("Patient Added Successfully!!");
            }else{
                System.out.println("Failed to add Patient!!");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    
    // Method to view all patients stored in the database
    public void viewPatients(){
        String query = "select * from patients";
        try{
            // Prepare SQL query to retrieve all patient records from the database
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Display a header for the patient table
            System.out.println("Patients: ");
            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");
            // Iterate through the result set and print each patient's details
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, name, age, gender);
                System.out.println("+------------+--------------------+----------+------------+");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
        
    }
    

    // Method to check if a patient with a given ID exists in the database
    public boolean getPatientById(int id){
        String query = "SELECT * FROM patients WHERE id = ?";
        try{
            // Prepare SQL query to retrieve a patient by ID
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // Set the ID parameter for the prepared statement
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Check if a result is returned, indicating the existence of the patient
            if(resultSet.next()){
                return true;
            }else{
                return false;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    
   // This Method use to Update the Patients Details 
    public void updatePatientDetails() {
        System.out.print("Enter Patient ID: ");
        int id = scanner.nextInt();
        // Check if the patient exists
        if(getPatientById(id)) {
            System.out.print("Enter New Patient Name: ");
            String newName = scanner.next();
            // Validate input for patient's age
            int newAge = -1;
            while (newAge == -1) {
                try {
                    System.out.print("Enter New Patient Age: ");
                    newAge = scanner.nextInt();
                    if (newAge < 0) {
                        System.out.println("Age cannot be negative. Please enter a valid age.");
                        newAge = -1;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid age.");
                    scanner.next(); // Clear the invalid input from the scanner
                }
            }
            System.out.print("Enter New Patient Gender: ");
            String newGender = scanner.next();
            
            try {
                // Prepare SQL query to update patient details
                String query = "UPDATE patients SET name=?, age=?, gender=? WHERE id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, newName);
                preparedStatement.setInt(2, newAge);
                preparedStatement.setString(3, newGender);
                preparedStatement.setInt(4, id);
                
                int affectedRows = preparedStatement.executeUpdate();
                // Check if the operation was successful and provide appropriate feedback
                if(affectedRows > 0) {
                    System.out.println("Patient Details Updated Successfully!!");
                } else {
                    System.out.println("Failed to Update Patient Details!!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Patient with ID " + id + " does not exist.");
        }
    }
    
    
 // Method to search for patients by name
    public void searchPatientByName(String name) {
        String query = "SELECT * FROM patients WHERE name LIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Search Results for Patient Name '" + name + "':");
            System.out.println("+------------+--------------------+----------+------------+");
            System.out.println("| Patient Id | Name               | Age      | Gender     |");
            System.out.println("+------------+--------------------+----------+------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String patientName = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String gender = resultSet.getString("gender");
                System.out.printf("| %-10s | %-18s | %-8s | %-10s |\n", id, patientName, age, gender);
                System.out.println("+------------+--------------------+----------+------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 // Method to view appointments for a specific patient
    public void viewAppointmentsByPatient() {
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        String query = "SELECT a.id AS appointment_id, d.name AS doctor_name, a.appointment_date " +
                       "FROM appointments a " +
                       "JOIN doctors d ON a.doctor_id = d.id " +
                       "WHERE a.patient_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, patientId);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Appointments for Patient ID " + patientId + ":");
            System.out.println("+------------+--------------------+---------------------+");
            System.out.println("| Appointment| Doctor Name        | Date                |");
            System.out.println("+------------+--------------------+---------------------+");
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String doctorName = resultSet.getString("doctor_name");
                String appointmentDate = resultSet.getString("appointment_date");
                System.out.printf("| %-10s | %-18s | %-20s |\n", appointmentId, doctorName, appointmentDate);
                System.out.println("+------------+--------------------+---------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}


