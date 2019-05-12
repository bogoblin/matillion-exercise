import main.java.Exercise1;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class Exercise1Test {
    @Test
    public void testDifferenceBetweenTwoEqualStringsIsZero() {
        try {
            assertEquals(0, Exercise1.stringDifference("cat", "cat"));
        } catch (Exercise1.StringLengthMismatch e) {
            fail();
        }
    }

    @Test
    public void testDifferenceBetweenEmptyStringsIsZero() {
        try {
            assertEquals(0, Exercise1.stringDifference("", ""));
        } catch (Exercise1.StringLengthMismatch e) {
            fail();
        }
    }

    @Test
    public void testDifferenceBetweenExampleStringsIsCorrect() {
        try {
            assertEquals(5, Exercise1.stringDifference("D23W8MCCIZQOP9", "D236862CEZQOPS"));
        } catch (Exercise1.StringLengthMismatch e) {
            fail();
        }
    }

    @Test
    public void testDifferenceBetweenStringsOfDifferentLengthThrowsException() {
        try {
            Exercise1.stringDifference("asdf", "asd");
        } catch (Exercise1.StringLengthMismatch e) {
            assertEquals("Strings must be of equal length.", e.getMessage());
        }

        try {
            Exercise1.stringDifference("asdf", "asdfgh");
        } catch (Exercise1.StringLengthMismatch e) {
            assertEquals("Strings must be of equal length.", e.getMessage());
        }
    }
}
