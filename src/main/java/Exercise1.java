public class Exercise1 {

    /**
     * Given two strings as command line arguments,
     * output the difference between the two strings.
     * @param args
     */
    public static void main(String[] args) {
        try {
            System.out.println(stringDifference(args[1], args[2]));
        } catch (StringLengthMismatch e) {
            System.err.println(e.getMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Please provide two strings as command line arguments.");
        }
    }

    /**
     * Find the difference between two strings, a and b.
     * @param a the first string
     * @param b the second string
     * @return an integer, the number of characters that differ between the two strings.
     */
    public static int stringDifference(String a, String b) throws StringLengthMismatch {
        if (a.length() != b.length()) {
            throw new StringLengthMismatch("Strings must be of equal length.");
        }
        int difference = 0;
        for (int i=0; i<a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                difference++;
            }
        }
        return difference;
    }

    public static class StringLengthMismatch extends Exception {
        public StringLengthMismatch(String s) {
            super(s);
        }
    }
}
