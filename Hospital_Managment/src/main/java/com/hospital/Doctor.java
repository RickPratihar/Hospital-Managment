package com.hospital;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

// Class representing a Doctor entity and handling database operations related to doctors
public class Doctor {
    private Connection connection; // Connection object for database interaction

    // Constructor to initialize the Doctor object with a database connection
    public Doctor(Connection connection){
        this.connection = connection;
    }

    // Method to view all doctors stored in the database
    public void viewDoctors(){
        String query = "select * from doctors";
        try{
            // Prepare SQL query to retrieve all doctor records from the database
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Display a header for the doctor table
            System.out.println("Doctors: ");
            System.out.println("+------------+--------------------+------------------+");
            System.out.println("| Doctor Id  | Name               | Specialization   |");
            System.out.println("+------------+--------------------+------------------+");
            // Iterate through the result set and print each doctor's details
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("| %-10s | %-18s | %-16s |\n", id, name, specialization);
                System.out.println("+------------+--------------------+------------------+");
            }

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    

    // Method to check if a doctor with a given ID exists in the database
    public boolean getDoctorById(int id){
        String query = "SELECT * FROM doctors WHERE id = ?";
        try{
            // Prepare SQL query to retrieve a doctor by ID
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            // Set the ID parameter for the prepared statement
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Check if a result is returned, indicating the existence of the doctor
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
    
    
 // Method to view appointments along with doctor names
    public void viewAppointmentsWithDoctors() {
        String query = "SELECT a.id AS appointment_id, p.name AS patient_name, a.appointment_date, d.name AS doctor_name " +
                       "FROM appointments a " +
                       "JOIN patients p ON a.patient_id = p.id " +
                       "JOIN doctors d ON a.doctor_id = d.id";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Appointments with Doctors:");
            System.out.println("+------------+------------------+---------------------+-------------------+");
            System.out.println("| Appointment| Patient Name     | Date                | Doctor Name       |");
            System.out.println("+------------+------------------+---------------------+-------------------+");
            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String patientName = resultSet.getString("patient_name");
                String appointmentDate = resultSet.getString("appointment_date");
                String doctorName = resultSet.getString("doctor_name");
                System.out.printf("| %-10s | %-16s | %-20s | %-16s |\n", appointmentId, patientName, appointmentDate, doctorName);
                System.out.println("+------------+------------------+---------------------+-------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
 // Method to search for doctors by name
    public void searchDoctorByName(String name) {
        String query = "SELECT * FROM doctors WHERE name LIKE ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + name + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("Search Results for Doctor Name '" + name + "':");
            System.out.println("+------------+--------------------+------------------+");
            System.out.println("| Doctor Id  | Name               | Specialization   |");
            System.out.println("+------------+--------------------+------------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("id");
                String doctorName = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("| %-10s | %-18s | %-16s |\n", id, doctorName, specialization);
                System.out.println("+------------+--------------------+------------------+");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

