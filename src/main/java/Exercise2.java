package main.java;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class Exercise2 {
    private static String USERNAME = "technical_test";
    private static String PASSWORD = "HopefullyProspectiveDevsDontBreakMe";
    private static String HOSTNAME = "mysql-technical-test.cq5i4y35n9gg.eu-west-1.rds.amazonaws.com";
    private static String DATABASE = "foodmart";

    private static Connection conn;
    private static PreparedStatement preparedStatement;

    public static void main(String[] args) {
        // ask for input - department, then pay type, then education level

        // Connect to the database
        try {
            connect();
        } catch (SQLException e) {
            System.err.println("Unable to connect to database, exiting.");
            System.err.println(e.getMessage());
            return;
        }

        // Prepare the statement to run queries on
        try {
            prepareStatement();
        } catch (SQLException e) {
            System.err.println("Unable to prepare statement, exiting.");
            System.err.println(e.getMessage());
            return;
        }

        // query database
        try {
            EmployeesResult er = getEmployees(
                    "Store Management", "Monthly", "Bachelors Degree");
            System.out.println(er);
        } catch (NoConnectionException e) {
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error executing query.");
            System.err.println(e.getMessage());
        }

    }

    public static void connect() throws SQLException
    {
        conn = DriverManager.getConnection(
                "jdbc:mysql://"+HOSTNAME+"/"+DATABASE, USERNAME, PASSWORD);
    }

    public static void prepareStatement() throws SQLException {
        preparedStatement = conn.prepareStatement(
                "SELECT employee_id, full_name, position.position_title, " +
                        "department_description, employee.management_role, salary, " +
                        "pay_type, education_level " +
                        "FROM employee " +
                        "JOIN department " +
                        "ON department.department_id = employee.department_id " +
                        "JOIN position " +
                        "ON position.position_id = employee.position_id " +
                        "WHERE department_description = ? " +
                        "AND pay_type = ? " +
                        "AND education_level = ?;");
    }

    public static class NoConnectionException extends Exception {
        public NoConnectionException(String s) {
            super(s);
        }
    }

    public static EmployeesResult getEmployees(
            String department, String payType, String educationLevel)
            throws SQLException, NoConnectionException
    {
        if (conn == null) {
            throw new NoConnectionException(
                    "No connection to database. Call connect() first.");
        }

        preparedStatement.setString(1, department);
        preparedStatement.setString(2, payType);
        preparedStatement.setString(3, educationLevel);

        preparedStatement.execute();

        return new EmployeesResult(preparedStatement.getResultSet());
    }

    public static class EmployeesResult {

        public int getColumnCount() {
            return columnCount;
        }

        public String[] getColumnLabels() {
            return columnLabels;
        }

        public int getNumberOfResults() {
            return numberOfResults;
        }

        public ArrayList<String[]> getResults() {
            return results;
        }

        private int columnCount;
        private String[] columnLabels;
        private int numberOfResults;
        private ArrayList<String[]> results;

        /**
         * EmployeesResult stores the result from an SQL query.
         * @param rs a ResultSet object from an SQL query
         */
        public EmployeesResult(ResultSet rs) {
            try {
                ResultSetMetaData rsmd = rs.getMetaData();
                columnCount = rsmd.getColumnCount();
                columnLabels = new String[columnCount];

                // Store the column labels as a list
                // Note that result arrays start at 1, so 0th element will be null
                for (int i=1; i<columnCount; i++) {
                    columnLabels[i] = rsmd.getColumnLabel(i);
                }

                // Each row in the result is then stored as an array of strings.
                // Their values can be indexed, using the same indices from columnLabels.
                // An list of these is stored.

                numberOfResults = 0;
                results = new ArrayList<>();

                while (rs.next()) {
                    String[] currentResult = new String[columnCount];
                    for (int i=1; i<columnCount; i++) {
                        currentResult[i] = rs.getString(i);
                    }
                    numberOfResults++;
                    results.add(currentResult);
                }
            } catch (SQLException e) {
                System.err.println(e);
            } finally {
                // We can now release the ResultSet
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.err.println(e);
                }
            }
        }

        /**
         * Create a string for printing, showing each employee from the results.
         * @return
         */
        public String toString() {
            StringBuilder outputBuilder = new StringBuilder();

            outputBuilder.append(String.format("Found %d results:\n", numberOfResults));

            for (String[] result : results) {
                outputBuilder.append("------------------------------\n");
                for (int i=1; i<columnCount; i++) {
                    outputBuilder.append(columnLabels[i] + ": ");
                    outputBuilder.append(result[i] + "\n");
                }
            }
            return outputBuilder.toString();
        }
    }
}
