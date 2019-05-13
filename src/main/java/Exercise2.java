import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Exercise2 {
    // Credentials for defaultConnection()
    private static String USERNAME = "technical_test";
    private static String PASSWORD = "HopefullyProspectiveDevsDontBreakMe";
    private static String HOSTNAME = "mysql-technical-test.cq5i4y35n9gg.eu-west-1.rds.amazonaws.com";
    private static String DATABASE = "foodmart";

    public static void main(String[] args) {
        try {
            // Make a connection to the database
            Connection conn = defaultConnection();

            EmployeeFinder ef = new EmployeeFinder(conn);

            boolean exit = false;
            Scanner scanner = new Scanner(System.in);

            // This section handles user input - the program allows users to
            // make multiple queries to the database, so this must loop until
            // the user asks to exit.
            while (!exit) {
                System.out.print("Enter a department: ");
                String department = scanner.nextLine();

                System.out.print("Enter a pay type: ");
                String payType = scanner.nextLine();

                System.out.print("Enter an education level: ");
                String educationLevel = scanner.nextLine();

                ArrayList<Employee> employees = ef.findEmployees(department, payType, educationLevel);

                // Output the list of employees that were found
                int numberOfEmployeesFound = employees.size();
                System.out.println(String.format("%d employees found:", numberOfEmployeesFound));
                for (Employee e : employees) {
                    System.out.println(e);
                }

                // Ask the user if they want to run another query.
                while(true) {
                    System.out.print("Run another query? (y/n): ");
                    String yesOrNo = scanner.nextLine();
                    if (yesOrNo.equalsIgnoreCase("n")) {
                        exit = true; // break from the outer while loop also
                        break;
                    } else if (yesOrNo.equalsIgnoreCase("y")) {
                        exit = false; // continue the outer while loop
                        break;
                    } else {
                        System.out.println("Please enter y or n.");
                    }
                }
            }

            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("Unable to close database connection. Exiting.");
            }

        } catch (DatabaseConnectionException e) {
            System.out.println("Unable to connect to database. Exiting.");
            System.err.println(e.getMessage());
        } catch (PrepareStatementException e) {
            System.out.println("Unable to prepare statement. Exiting.");
            System.err.println(e.getMessage());
        } catch (FindEmployeesException e) {
            System.out.println("Problem occurred when finding employees. Exiting.");
            System.err.println(e.getMessage());
        }

    }

    /**
     * Get the default connection to the database.
     * Credentials for this are specified in the Exercise2 class.
     * @return an SQL database connection
     * @throws DatabaseConnectionException if unable to connect to the database
     */
    public static Connection defaultConnection() throws DatabaseConnectionException {
        try {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + HOSTNAME + "/" + DATABASE, USERNAME, PASSWORD);
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e.getMessage());
        }
    }

    public static class EmployeeFinder {
        private Connection conn;
        private PreparedStatement preparedStatement;

        /**
         * Creating an EmployeeFinder prepares a statement used to find employees
         * @param requiredConn a connection to an SQL database
         * @throws PrepareStatementException if preparing the statement fails.
         */
        public EmployeeFinder(Connection requiredConn) throws PrepareStatementException {
            conn = requiredConn;

            // Prepare the statement used to find employees
            try {
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
            } catch (SQLException e) {
                throw new PrepareStatementException(e.getMessage());
            }

        }

        /**
         * Find employees matching the parameters
         * @param department
         * @param payType
         * @param educationLevel
         * @return A list of employees
         * @throws FindEmployeesException
         */
        public ArrayList<Employee> findEmployees(String department, String payType, String educationLevel)
        throws FindEmployeesException
        {
            try {
                // Execute the prepared statement with the given arguments
                preparedStatement.setString(1, department);
                preparedStatement.setString(2, payType);
                preparedStatement.setString(3, educationLevel);
                preparedStatement.execute();
                ResultSet rs = preparedStatement.getResultSet();

                ArrayList<Employee> employees = new ArrayList<>();

                // Calling rs.next() moves the cursor to the next result.
                // If it returns false, then there are no more results in the result set.
                while (rs.next()) {
                    employees.add(new Employee(rs));
                }


                return employees;
            } catch (SQLException e) {
                throw new FindEmployeesException(e.getMessage());
            }
        }
    }

    public static class Employee {
        private int id;
        private String fullName;
        private String positionTitle;
        private String departmentDescription;
        private String managementRole;
        private String salary;
        private String payType;
        private String educationLevel;

        public int getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getPositionTitle() {
            return positionTitle;
        }

        public String getDepartmentDescription() {
            return departmentDescription;
        }

        public String getManagementRole() {
            return managementRole;
        }

        public String getSalary() {
            return salary;
        }

        public String getPayType() {
            return payType;
        }

        public String getEducationLevel() {
            return educationLevel;
        }

        /**
         * Create an employee from the current result in a result set.
         * Call rs.next() before using this constructor.
         * @param rs the result set to create the employee from
         */
        public Employee(ResultSet rs) throws SQLException {
            id = rs.getInt(1);
            fullName = rs.getString(2);
            positionTitle = rs.getString(3);
            departmentDescription = rs.getString(4);
            managementRole = rs.getString(5);
            salary = rs.getString(6);
            payType = rs.getString(7);
            educationLevel = rs.getString(8);
        }

        public String toString() {
            StringBuilder out = new StringBuilder();

            out.append(String.format("Employee id: %d\n", id));
            out.append(String.format("Full name: %s\n", fullName));
            out.append(String.format("Position: %s\n", positionTitle));
            out.append(String.format("Department: %s\n", departmentDescription));
            out.append(String.format("Management Role: %s\n", managementRole));
            out.append(String.format("Salary: %s\n", salary));
            out.append(String.format("Pay Type: %s\n", payType));
            out.append(String.format("Education: %s\n", educationLevel));

            return out.toString();
        }
    }

    public static class PrepareStatementException extends Exception {
        public PrepareStatementException(String message) {
            super(message);
        }
    }
    public static class FindEmployeesException extends Exception {
        public FindEmployeesException(String message) {
            super(message);
        }
    }
    public static class DatabaseConnectionException extends Exception {
        public DatabaseConnectionException(String message) { super(message); }
    }
}
