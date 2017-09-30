package org.bitbucket.ytimes.client.kkm;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.iki.elonen.NanoHTTPD;
import org.bitbucket.ytimes.client.kkm.printer.Printer;
import org.bitbucket.ytimes.client.kkm.printer.PrinterException;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Created by andrey on 01.10.17.
 */
@Component
public class KKMWebServer extends NanoHTTPD {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Printer printer;

    @Value("${websocket.code}")
    private String code;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public KKMWebServer(@Value("${websocket.port}") int port) {
        super(port);
        onStart(port);
    }

    private void onStart(int port) {
        try {
            logger.info("KKM server started: ");

            boolean hasAddress = false;
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets)) {
                Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                    if (inetAddress.toString().startsWith("/192")) {
                        logger.info("  Адрес устройства: " + inetAddress.toString().substring(1));
                        hasAddress = true;
                    }
                    else if (inetAddress.toString().startsWith("192")) {
                        logger.info("  Адрес устройства: " + inetAddress.toString());
                        hasAddress = true;
                    }
                }
            }

            if (!hasAddress) {
                logger.info("  Адрес устройства: неизвестно");
            }
            logger.info("  Открытый порт: " + port);
            logger.info("  Код подтверждения: " + code);
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }


    @Override
    public NanoHTTPD.Response serve(NanoHTTPD.IHTTPSession session) {
        NanoHTTPD.Method method = session.getMethod();
        NanoHTTPD.Response response = null;
        if (method.equals(NanoHTTPD.Method.POST)) {
            final HashMap<String, String> map = new HashMap<String, String>();
            try {
                session.parseBody(map);
                final String json = map.get("postData");

                logger.info("received: " + json);

                Result res = new Result();
                res.success = true;
                try {
                    processAction(json);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    res.success = false;
                    res.errorMessage = e.getMessage();
                    res.errorClass = e.getClass().getSimpleName();
                }
                response = newFixedLengthResponse(mapper.writeValueAsString(res));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                response = newFixedLengthResponse("{success: false, errorMessage: \"" + e.getMessage() + "\"}");
            }
        }
        else {
            response = newFixedLengthResponse("");
        }
        response = addCORSHeaders(response);
        return response;
    }

    private NanoHTTPD.Response addCORSHeaders(NanoHTTPD.Response resp) {
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.addHeader("Access-Control-Allow-Headers", "origin,accept,content-type");
        resp.addHeader("Access-Control-Allow-Credentials", "true");
        resp.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        resp.addHeader("Access-Control-Max-Age", "" + (42 * 60 * 60));
        return resp;
    }

    private void processAction(String json) throws PrinterException, IOException {
        ActionRecord action = parseMessage(json, ActionRecord.class);
        if (action == null) {
            throw new IllegalArgumentException("error parse ActionRecord");
        }
        logger.info("Обработка действия: " + action.action);
        if ("newGuest".equals(action.action)) {
            NewGuestCommandRecord record = parseMessage(action.data, NewGuestCommandRecord.class);
            checkCode(record.code);
            printer.printNewGuest(record);
        }
        else if ("printCheck".equals(action.action)) {
            PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
            checkCode(record.code);
            printer.printCheck(record);
        }
        else if ("printReturnCheck".equals(action.action)) {
            PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
            checkCode(record.code);
            printer.printReturnCheck(record);
        }
        else if ("printPredCheck".equals(action.action)) {
            PrintCheckCommandRecord record = parseMessage(action.data, PrintCheckCommandRecord.class);
            checkCode(record.code);
            printer.printPredCheck(record);
        }
        else if ("reportX".equals(action.action)) {
            ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
            checkCode(record.code);
            printer.reportX();
        }
        else if ("reportZ".equals(action.action)) {
            ReportCommandRecord record = parseMessage(action.data, ReportCommandRecord.class);
            checkCode(record.code);
            printer.reportZ();
        }
        else {
            throw new IllegalArgumentException("Неизвестная команда: " + action.action);
        }
        logger.info("Обработано действие: " + action.action);
    }

    private void checkCode(String code) throws PrinterException {
        if (code == null || code.trim().isEmpty() || !code.equals(this.code)) {
            throw new PrinterException(0, "Неизвестная команда. Проверьте настройки системы");
        }
    }

    private <T> T parseMessage(String message, Class<T> tClass) throws IOException {
        return mapper.readValue(message, tClass);
    }

}
