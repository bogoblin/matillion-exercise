package main.java;

public class Exercise1 {

    /**
     * Given two strings as command line arguments, output the difference between the two strings.
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(stringDifference(args[1], args[2]));
    }

    /**
     * Find the difference between two strings, a and b.
     * @param a the first string
     * @param b the second string
     * @return an integer, the number of characters that differ between the two strings.
     */
    public static int stringDifference(String a, String b) {
        int difference = 0;
        for (int i=0; i<a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                difference++;
            }
        }
        return difference;
    }
}