package pt.up.fe.aiad.tests;

import org.junit.Test;
import pt.up.fe.aiad.utils.IPAddressValidator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IPAddressValidatorTest {

    @Test
    public void testValidate() throws Exception {
        String validIPs[] = new String[] { "1.1.1.1", "255.255.255.255", "192.168.1.1", "10.10.1.1",
                "132.254.111.10", "26.10.2.10", "127.0.0.1" };

        String validIPWithPorts[] = new String[] { "1.1.1.1:1", "255.255.255.255:20000", "192.168.1.1:65536", "10.10.1.1:20",
                "132.254.111.10:12345", "26.10.2.10:000", "127.0.0.1:1299" };

        String invalidIPs[] = new String[] { "10.10.10", "10.10", "10", "a.a.a.a",
                "10.0.0.a", "10.10.10.256", "222.222.2.999",
                "999.10.10.20", "2222.22.22.22", "22.2222.22.2",
                "10.10.10", "10.10.10" };

        String invalidIPWithPorts[] = new String[] { "10.10.10", "10.10", "10", "a.a.a.a",
                "132.254.111.10", "26.10.2.10:", "127.0.0.1:aaa",
                "1.1.1.1:123456", "255.255.255.255:11a11" };

        for (String validIP : validIPs) {
            assertTrue(IPAddressValidator.validate(validIP, false));
        }

        for (String validIPWithPort : validIPWithPorts) {
            assertTrue(IPAddressValidator.validate(validIPWithPort, true));
        }

        for (String invalidIP : invalidIPs) {
            assertFalse(IPAddressValidator.validate(invalidIP, false));
        }

        for (String invalidIPWithPort : invalidIPWithPorts) {
            assertFalse(IPAddressValidator.validate(invalidIPWithPort, true));
        }
    }
}
