package pt.up.fe.aiad.tests;

import org.junit.Test;
import pt.up.fe.aiad.utils.StringUtils;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class StringUtilsTest {

    @Test
    public void testIsNullOrEmpty() throws Exception {
        assertTrue(StringUtils.isNullOrEmpty(null));
        assertTrue(StringUtils.isNullOrEmpty(" "));
        assertTrue(StringUtils.isNullOrEmpty("  "));
        assertTrue(StringUtils.isNullOrEmpty("\t"));
        assertTrue(StringUtils.isNullOrEmpty(""));
        assertTrue(StringUtils.isNullOrEmpty(" \t "));

        assertFalse(StringUtils.isNullOrEmpty("a"));
        assertFalse(StringUtils.isNullOrEmpty(" - "));
        assertFalse(StringUtils.isNullOrEmpty("\tb\t"));
    }
}
