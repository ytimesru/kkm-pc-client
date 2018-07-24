package org.bitbucket.ytimes.client.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
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
    private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

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

    public static String toDateTimeString(Date date) {
        if (date == null) {
            return null;
        }
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return dateTimeFormat.format(date.getTime());
    }


    public static Integer calcPricePercent(Integer price, Integer percent) {
        if (price == null || price == 0) {
            return 0;
        }
        return new BigDecimal(price).multiply(new BigDecimal(percent)).divide(new BigDecimal(100.0)).intValue();
    }

    public static Double roundTo(Double value, int count) {
        if (value == null) {
            value = 0.0;
        }
        return roundTo(new BigDecimal(value), count);
    }

    public static Double roundTo(BigDecimal value, int count) {
        return value.setScale(count, BigDecimal.ROUND_HALF_UP).doubleValue();
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
