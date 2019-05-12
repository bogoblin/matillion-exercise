package test.java;

import main.java.Exercise2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.SQLException;

public class Exercise2Test {
    @BeforeAll
    public static void setup() {
        try {
            Exercise2.connect();
            Exercise2.prepareStatement();
        } catch (SQLException e) {

        }
    }

    @Test
    // This test is clearly fragile - just using it to check correctness as I go
    public void integrationTest1() {
        try {
            Exercise2.EmployeesResult er = Exercise2.getEmployees(
                    "Store Management", "Monthly", "Bachelors Degree");
            assertEquals(8, er.getColumnCount());
            assertEquals(27, er.getNumberOfResults());
        } catch (SQLException e) {
            fail();
        } catch (Exercise2.NoConnectionException e) {
            fail();
        }

    }
}
