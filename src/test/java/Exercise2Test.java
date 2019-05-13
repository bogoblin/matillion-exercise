import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;
import static org.mockito.Matchers.anyString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Exercise2Test {
    @Test
    public void testCreatingEmployee() {
        ResultSet rs = mock(ResultSet.class);

        try {
            when(rs.getInt(1)).thenReturn(1);
            when(rs.getString(2)).thenReturn("Test Name");
            when(rs.getString(3)).thenReturn("Test Position Title");
            when(rs.getString(4)).thenReturn("Test Department Description");
            when(rs.getString(5)).thenReturn("Test Management Role");
            when(rs.getString(6)).thenReturn("4000");
            when(rs.getString(7)).thenReturn("Test Pay Type");
            when(rs.getString(8)).thenReturn("Test Education Level");

            Exercise2.Employee test = new Exercise2.Employee(rs);
            assertEquals(1, test.getId());
            assertEquals("Test Name", test.getFullName());
            assertEquals("Test Position Title", test.getPositionTitle());
            assertEquals("Test Department Description", test.getDepartmentDescription());
            assertEquals("Test Management Role", test.getManagementRole());
            assertEquals("4000", test.getSalary());
            assertEquals("Test Pay Type", test.getPayType());
            assertEquals("Test Education Level", test.getEducationLevel());
        } catch (SQLException e) {
            fail();
        }
    }

    @Test
    public void testStatementIsPrepared() {
        Connection conn = mock(Connection.class);

        try {
            Exercise2.EmployeeFinder ef = new Exercise2.EmployeeFinder(conn);

            verify(conn).prepareStatement("SELECT employee_id, full_name, position.position_title, " +
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
            fail();
        }
    }

    @Test
    public void testFindEmployeesWithNoResults() {
        Connection conn = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        try {
            // When we create the employee finder, inject the mock prepared statement
            when(conn.prepareStatement(anyString())).thenReturn(ps);

            when(ps.getResultSet()).thenReturn(rs);
            when(rs.next()).thenReturn(false);

            // Create the employee finder
            Exercise2.EmployeeFinder ef = new Exercise2.EmployeeFinder(conn);

            ArrayList<Exercise2.Employee> employees = ef.findEmployees(
                    "Dep", "Pay", "Edu");

            // Since rs.next() returns false, the list of employees should be empty
            assertEquals(true, employees.isEmpty());

            // It is important that these are executed in the correct order
            InOrder psInOrder = Mockito.inOrder(ps);
            psInOrder.verify(ps).setString(1, "Dep");
            psInOrder.verify(ps).setString(2, "Pay");
            psInOrder.verify(ps).setString(3, "Edu");
            psInOrder.verify(ps).execute();
            psInOrder.verify(ps).getResultSet();
        } catch (SQLException e) {
            fail();
        }
    }
}
