package org.bitbucket.ytimes.client.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitbucket.ytimes.client.screen.record.ScreenInfoRecord;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class WSServer extends WebSocketServer {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private ObjectMapper mapper = new ObjectMapper();
    private static ScreenInfoRecord info;

    @Autowired
    public WSServer(@Value("${websocket.port}") int port) {
        super(new InetSocketAddress(port));
        logger.info("start ws on port: " + port);
    }

    public void setInfo(ScreenInfoRecord record) {
        info = record;
        sendToAll();
    }

    private void sendToAll() {
        try {
            broadcast(info != null ? mapper.writeValueAsString(info) : "clear");
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " connected");
        sendToAll();
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        logger.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + " disconnected");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        logger.info(conn.getRemoteSocketAddress().getAddress().getHostAddress() + ": " + message );
    }

    @Override
    public void onError(WebSocket conn, Exception e) {
        logger.error(e.getMessage(), e);
    }

    @Override
    public void onStart() {
        logger.info("ws server started");
    }


}
