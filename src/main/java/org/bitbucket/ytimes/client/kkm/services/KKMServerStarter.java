package org.bitbucket.ytimes.client.kkm.services;

import fi.iki.elonen.NanoHTTPD;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;

/**
 * Created by root on 27.05.17.
 */
@Component
public class KKMServerStarter {

    @Autowired
    private KKMWebServer server;

    @PostConstruct
    public void init() throws Exception {
        SSLContext context = getSSLContext();
        server.makeSecure(context.getServerSocketFactory(), null);
        server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    private SSLContext getSSLContext() throws Exception {
        InputStream keystoreInput = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream("ytimes.jks");
        SSLContext context = getSSLFactories(keystoreInput, "ytimes");
        keystoreInput.close();
        return context;
    }

    private SSLContext getSSLFactories(InputStream keyStream, String keyStorePassword) throws Exception {
        // Get keyStore
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        char[] keyPassword = keyStorePassword.toCharArray();

        // load the stream to your store
        keyStore.load(keyStream, keyPassword);

        // initialize a trust manager factory with the trusted store
        KeyManagerFactory keyFactory =
                KeyManagerFactory.getInstance("SunX509");
        keyFactory.init(keyStore, keyPassword);

        // get the trust managers from the factory
        KeyManager[] keyManagers = keyFactory.getKeyManagers();

        // Now get trustStore
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

        // if your store is password protected then declare it (it can be null however)
        //char[] trustPassword = password.toCharArray();

        // initialize a trust manager factory with the trusted store
        TrustManagerFactory trustFactory =
                TrustManagerFactory.getInstance("SunX509");
        trustFactory.init(keyStore);

        // get the trust managers from the factory
        TrustManager[] trustManagers = trustFactory.getTrustManagers();

        // initialize an ssl context to use these managers and set as default
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        SSLContext.setDefault(sslContext);
        return sslContext;
    }

}
