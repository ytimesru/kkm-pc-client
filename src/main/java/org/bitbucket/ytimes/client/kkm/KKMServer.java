package org.bitbucket.ytimes.client.kkm;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitbucket.ytimes.client.kkm.printer.Printer;
import org.bitbucket.ytimes.client.kkm.printer.PrinterException;
import org.bitbucket.ytimes.client.kkm.record.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;

/**
 * Created by andrey on 27.05.17.
 */
@Component
public class KKMServer extends WebSocketServer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Printer printer;

    @Value("${websocket.code}")
    private String code;

    private Integer port;


    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public KKMServer(@Value("${websocket.port}") int port) {
        super(new InetSocketAddress(port));
        this.port = port;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        logger.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message );

        ActionRecord action = parseMessage(conn, message, ActionRecord.class);
        if (action == null) {
            return;
        }
        try {
            long start = System.currentTimeMillis();
            if ("newGuest".equals(action.action)) {
                NewGuestCommandRecord record = parseMessage(conn, action.data, NewGuestCommandRecord.class);
                checkCode(record.code);
                try {
                    printer.printNewGuest(record);
                }
                catch (PrinterException e) {
                    if (e.getCode() == -11 || e.getCode() == -1) {
                        printer.connect();
                        printer.printNewGuest(record);
                    }
                    else {
                        throw e;
                    }
                }
            }
            else if ("printCheck".equals(action.action)) {
                PrintCheckCommandRecord record = parseMessage(conn, action.data, PrintCheckCommandRecord.class);
                checkCode(record.code);
                try {
                    printer.printCheck(record);
                }
                catch (PrinterException e) {
                    if (e.getCode() == -11 || e.getCode() == -1) {
                        printer.connect();
                        printer.printCheck(record);
                    }
                    else {
                        throw e;
                    }
                }
            }
            else if ("printPredCheck".equals(action.action)) {
                PrintCheckCommandRecord record = parseMessage(conn, action.data, PrintCheckCommandRecord.class);
                checkCode(record.code);
                try {
                    printer.printPredCheck(record);
                }
                catch (PrinterException e) {
                    if (e.getCode() == -11 || e.getCode() == -1) {
                        printer.connect();
                        printer.printPredCheck(record);
                    }
                    else {
                        throw e;
                    }
                }
            }
            else if ("reportX".equals(action.action)) {
                ReportCommandRecord record = parseMessage(conn, action.data, ReportCommandRecord.class);
                checkCode(record.code);

                try {
                    printer.reportX();
                }
                catch (PrinterException e) {
                    if (e.getCode() == -11 || e.getCode() == -1) {
                        printer.connect();
                        printer.reportX();
                    }
                    else {
                        throw e;
                    }
                }

            }
            else if ("reportZ".equals(action.action)) {
                ReportCommandRecord record = parseMessage(conn, action.data, ReportCommandRecord.class);
                checkCode(record.code);

                try {
                    printer.reportZ();
                }
                catch (PrinterException e) {
                    if (e.getCode() == -11 || e.getCode() == -1) {
                        printer.connect();
                        printer.reportZ();
                    }
                    else {
                        throw e;
                    }
                }
            }
            else {
                sendError(conn, "kkm server", "Неизвестная команда: " + action.action);
            }

            long end = System.currentTimeMillis();
            logger.info("Time: " + (end - start) + "ms");

            Result result = new Result();
            result.success = true;
            try {
                conn.send(mapper.writeValueAsString(result));
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        catch (PrinterException e) {
            processException(conn, e);
        }
    }

    private void checkCode(String code) throws PrinterException {
        if (StringUtils.isEmpty(code) || !code.equals(this.code)) {
            throw new PrinterException(0, "Неизвестная команда. Проверьте настройки системы");
        }
    }

    private <T> T parseMessage(WebSocket conn, String message, Class<T> tClass) {
        try {
            return mapper.readValue(message, tClass);
        }
        catch (Exception e) {
            processException(conn, e);
            return null;
        }
    }

    private void processException(WebSocket conn, Exception e) {
        sendError(conn, e.getClass().getSimpleName(), e.getMessage());
    }

    private void sendError(WebSocket conn, String errorClass, String message) {
        Result result = new Result();
        result.success = false;
        result.errorClass = errorClass;
        result.errorMessage = message;
        try {
            conn.send(mapper.writeValueAsString(result));
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        logger.error(e.getMessage(), e);
        if( conn != null ) {
            processException(conn, e);
        }
    }

    @Override
    public void onStart() {
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

}
