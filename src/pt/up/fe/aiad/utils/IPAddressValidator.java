package pt.up.fe.aiad.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPAddressValidator {

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    private static final String IPADDRESS_WITH_PORT_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
                    "([01]?\\d\\d?|2[0-4]\\d|25[0-5]):" +
                    "[0-9]{1,5}$";

    private static Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
    private static Pattern patternWithPort = Pattern.compile(IPADDRESS_WITH_PORT_PATTERN);

    /**
     * Validate ip address with regular expression
     *
     * @param ip ip address for validation
     * @param withPort ip address needs port number
     * @return true valid ip address, false invalid ip address
     */
    public static boolean validate(final String ip, boolean withPort) {
        Matcher matcher = withPort ? patternWithPort.matcher(ip) : pattern.matcher(ip);
        return matcher.matches();
    }
}
