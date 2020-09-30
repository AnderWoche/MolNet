package de.moldiy.molnet.server;

import io.netty.handler.ssl.SslHandler;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SSLHandlerProvider {
    private static final Logger logger = Logger.getLogger(String.valueOf(SSLHandlerProvider.class));

    private static final String PROTOCOL = "TLS";
    private static final String ALGORITHM_SUN_X509 = "SunX509";
    private static final String ALGORITHM = "ssl.KeyManagerFactory.algorithm";
    private static final String KEYSTORE = "ssl_certs/mysslstore.jks";
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String KEYSTORE_PASSWORD = "123456";
    private static final String CERT_PASSWORD = "123456";
    private static SSLContext serverSSLContext = null;

    public static SslHandler getSSLHandler() {
        SSLEngine sslEngine = null;
        if (serverSSLContext == null) {
            logger.log(Level.SEVERE, "Server SSL context is null");
            System.exit(-1);
        } else {
            sslEngine = serverSSLContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);

        }
        return new SslHandler(sslEngine);
    }

    public static void initSSLContext() {

        logger.info("Initiating SSL context");
        String algorithm = Security.getProperty(ALGORITHM);
        if (algorithm == null) {
            algorithm = ALGORITHM_SUN_X509;
        }
        KeyStore ks = null;
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(SSLHandlerProvider.class.getClassLoader().getResource(KEYSTORE).getFile());
            ks = KeyStore.getInstance(KEYSTORE_TYPE);
            ks.load(inputStream, KEYSTORE_PASSWORD.toCharArray());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot load the keystore file", e);
        } catch (CertificateException e) {
            logger.log(Level.SEVERE, "Cannot get the certificate", e);
        } catch (NoSuchAlgorithmException e) {
            logger.log(Level.SEVERE, "Somthing wrong with the SSL algorithm", e);
        } catch (KeyStoreException e) {
            logger.log(Level.SEVERE, "Cannot initialize keystore", e);
        } finally {
            try {
                assert inputStream != null;
                inputStream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Cannot close keystore file stream ", e);
            }
        }
        try {

            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(algorithm);
            kmf.init(ks, CERT_PASSWORD.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();
            TrustManager[] trustManagers = null;

            serverSSLContext = SSLContext.getInstance(PROTOCOL);
            serverSSLContext.init(keyManagers, trustManagers, null);


        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize the server-side SSLContext", e);
        }


    }


}
