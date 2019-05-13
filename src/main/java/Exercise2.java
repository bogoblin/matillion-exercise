import java.sql.*;
import java.util.ArrayList;

public class Exercise2 {
    private static String USERNAME = "technical_test";
    private static String PASSWORD = "HopefullyProspectiveDevsDontBreakMe";
    private static String HOSTNAME = "mysql-technical-test.cq5i4y35n9gg.eu-west-1.rds.amazonaws.com";
    private static String DATABASE = "foodmart";

    public static void main(String[] args) {

    }

    public static class EmployeeFinder {
        private Connection conn;
        private PreparedStatement preparedStatement;

        public EmployeeFinder(Connection requiredConn) throws SQLException {
            conn = requiredConn;

            // Prepare the statement used to find employees
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

        public ArrayList<Employee> findEmployees(String department, String payType, String educationLevel)
        throws SQLException
        {
            // Execute the prepared statement with the given arguments
            preparedStatement.setString(1, department);
            preparedStatement.setString(2, payType);
            preparedStatement.setString(3, educationLevel);
            preparedStatement.execute();
            ResultSet rs = preparedStatement.getResultSet();

            ArrayList<Employee> employees = new ArrayList<>();

            while (rs.next()) {
                employees.add(new Employee(rs));
            }

            return employees;
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
    }
}
