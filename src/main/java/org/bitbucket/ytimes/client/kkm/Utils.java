package org.bitbucket.ytimes.client.kkm;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

public class Utils {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public static String toDateString(Date date) {
        if (date == null) {
            return null;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return dateFormat.format(date.getTime());
    }

    public static String getClientIP() throws Exception {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets)) {
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                if (inetAddress.toString().startsWith("/192")) {
                    return inetAddress.toString().substring(1);
                }
                else if (inetAddress.toString().startsWith("192")) {
                    return inetAddress.toString();
                }
                else if (inetAddress.toString().startsWith("/172")) {
                    return inetAddress.toString().substring(1);
                }
                else if (inetAddress.toString().startsWith("172")) {
                    return inetAddress.toString();
                }
                else if (inetAddress.toString().startsWith("/10")) {
                    return inetAddress.toString().substring(1);
                }
                else if (inetAddress.toString().startsWith("10")) {
                    return inetAddress.toString();
                }
            }
        }
        return null;
    }


}
