package org.bitbucket.ytimes.client.kkm;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.net.ssl.*;
import java.io.*;
import java.security.KeyStore;

/**
 * Created by andrey on 27.05.17.
 */
public class Main {

    public static void main( String[] args ) throws Exception {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext("root-context.xml");

        int port = 4900;
        try {
            port = Integer.parseInt( args[ 0 ] );
        } catch ( Exception ex ) {
        }
    }

}
