package org.bitbucket.ytimes.client.main;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;

public class Utils {
    protected static Logger logger = LoggerFactory.getLogger(Utils.class);
    protected static ObjectMapper mapper;
    protected static ObjectWriter writer;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    static {
        mapper = new ObjectMapper();
        writer = mapper.writer().withDefaultPrettyPrinter();
    }

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

    public static String objToJSON(Object obj) {
        try {
            return obj == null ? null : writer.writeValueAsString(obj);
        }
        catch (IOException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static <T> T parseMessage(String message, Class<T> tClass) throws IOException {
        return mapper.readValue(message, tClass);
    }

    public static <T> T parseMessage(String message, TypeReference<T> ref) throws IOException {
        return mapper.readValue(message, ref);
    }

}
