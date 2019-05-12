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

    public static void main(String[] args) {
        // ask for input - department, then pay type, then education level

        try {
            connect(USERNAME, PASSWORD, HOSTNAME, DATABASE);
        } catch (SQLException e) {
            System.err.println("Unable to connect to database, exiting.");
            System.err.println(e.getMessage());
            return;
        }

        // query database
        try {
            EmployeesResult er = getEmployees(
                    "Store Management", "Monthly", "Bachelors Degree");
            //System.out.println(er);
        } catch (NoConnectionException e) {
            System.err.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error executing query.");
            System.err.println(e.getMessage());
        }

    }

    public static void connect(
            String username, String password, String hostname, String database)
            throws SQLException
    {
        conn = DriverManager.getConnection(
                "jdbc:mysql://"+HOSTNAME+"/"+DATABASE, USERNAME, PASSWORD);
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

        Statement stmt = conn.createStatement();
        stmt.execute(
                "PREPARE stmt FROM 'SELECT * " +
                        "FROM employee " +
                        "JOIN department " +
                        "ON department.department_id = employee.department_id " +
                        "JOIN position " +
                        "ON position.position_id = employee.position_id " +
                        "WHERE department_description = ? " +
                        "AND pay_type = ? " +
                        "AND education_level = ?';");
        stmt.execute(String.format("SET @d = '%s';", department));
        stmt.execute(String.format("SET @p = '%s';", payType));
        stmt.execute(String.format("SET @e = '%s';", educationLevel));
        stmt.execute("EXECUTE stmt USING @d, @p, @e;");

        return new EmployeesResult(stmt.getResultSet());
    }

    public static class EmployeesResult {

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

        // These are the names of the columns that should be printed.
        private static String[] colsToPrintArray = {
                "employee_id", "full_name", "position_title",
                "department_description", "management_role", "salary",
                "pay_type", "education_level"
        };
        /**
         * Create a string for printing, showing each employee from the results.
         * @return
         */
        public String toString() {
            // First make a hashset of the columns to be printed.
            HashSet<String> colsToPrint = new HashSet<>(Arrays.asList(colsToPrintArray));

            // Then we can get the indices of the columns to be printed.
            ArrayList<Integer> colsToPrintIndices = new ArrayList<>();

            for (int i=1; i<columnCount; i++) {
                // Check if we want to print this column.
                if (colsToPrint.contains(columnLabels[i])) {
                    colsToPrintIndices.add(i);
                }
            }

            StringBuilder outputBuilder = new StringBuilder();

            outputBuilder.append(String.format("Found %d results:\n", numberOfResults));

            for (String[] result : results) {
                outputBuilder.append("------------------------------\n");
                for (int i : colsToPrintIndices) {
                    outputBuilder.append(columnLabels[i] + ": ");
                    outputBuilder.append(result[i] + "\n");
                }
            }
            return outputBuilder.toString();
        }
    }
}
