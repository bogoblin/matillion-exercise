package test.java;

import main.java.Exercise1;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Exercise1Test {
    @Test
    public void testDifferenceBetweenTwoEqualStringsIsZero() {
        assertEquals(0, Exercise1.stringDifference("cat", "cat"));
    }

    @Test
    public void testDifferenceBetweenEmptyStringsIsZero() {
        assertEquals(0, Exercise1.stringDifference("", ""));
    }

    @Test
    public void testDifferenceBetweenExampleStringsIsCorrect() {
        assertEquals(5, Exercise1.stringDifference("D23W8MCCIZQOP9", "D236862CEZQOPS"));
    }
}
