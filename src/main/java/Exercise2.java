package main.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Exercise2 {
    private static String USERNAME = "technical_test";
    private static String PASSWORD = "HopefullyProspectiveDevsDontBreakMe";
    private static String HOSTNAME = "mysql-technical-test.cq5i4y35n9gg.eu-west-1.rds.amazonaws.com";
    private static String DATABASE = "foodmart";

    public static void main(String[] args) {
        // ask for input - department, then pay type, then education level

        // query database
        getEmployees("Store Management", "Monthly", "Bachelors Degree");
    }

    public static void getEmployees(String department, String payType, String educationLevel) {

        // Firstly, connect to the database
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(
                    "jdbc:mysql://"+HOSTNAME+"/"+DATABASE, USERNAME, PASSWORD);
        } catch (SQLException e) {
            // something has gone wrong with the connection
            System.err.println("Unable to connect to database.");
            System.err.println(e);
        }
    }
}
