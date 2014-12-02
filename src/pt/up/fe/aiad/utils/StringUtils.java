package pt.up.fe.aiad.utils;

public class StringUtils {
    /**
     * Demonstrate checking for String that is null, empty, or white space
     * only using standard Java classes.
     *
     * @param string String to be checked for null, empty, or white space only.
     * @return {@code true} if provided String is null, is empty, or
     *    has only characters that are considered white space.
     */
    public static boolean isNullOrEmpty(final String string) {
        return string == null || string.isEmpty() || string.trim().isEmpty();
    }
}
