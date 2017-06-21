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

import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * Created by andrey on 27.05.17.
 */
@Component
public class KKMServer extends WebSocketServer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private Printer printer;

    @Value("${websocket.code}")
    private String code;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public KKMServer(@Value("${websocket.port}") int port) {
        super(new InetSocketAddress(port));
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
            if ("newGuest".equals(action.action)) {
                NewGuestCommandRecord record = parseMessage(conn, action.data, NewGuestCommandRecord.class);
                checkCode(record.code);
                printer.printNewGuest(record);
            }
            else if ("printCheck".equals(action.action)) {
                PrintCheckCommandRecord record = parseMessage(conn, action.data, PrintCheckCommandRecord.class);
                checkCode(record.code);
                printer.printCheck(record);
            }
            else if ("printPredCheck".equals(action.action)) {
                PrintCheckCommandRecord record = parseMessage(conn, action.data, PrintCheckCommandRecord.class);
                checkCode(record.code);
                printer.printPredCheck(record);
            }
            else if ("reportX".equals(action.action)) {
                ReportCommandRecord record = parseMessage(conn, action.data, ReportCommandRecord.class);
                checkCode(record.code);
                printer.reportX();
            }
            else if ("reportZ".equals(action.action)) {
                ReportCommandRecord record = parseMessage(conn, action.data, ReportCommandRecord.class);
                checkCode(record.code);
                printer.reportZ();
            }
            else {
                sendError(conn, "kkm server", "Неизвестная команда: " + action.action);
            }

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
            throw new PrinterException("Неизвестная команда. Проверьте настройки системы");
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
        logger.info("KKM server started");
    }

}
