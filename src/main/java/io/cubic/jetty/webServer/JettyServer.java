package io.cubic.jetty.webServer.sock;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Starts with Jetty an auto-executable web application packaged into a JAR file.
 */
public class JettyServer {

    public static final String WEBAPP_RESOURCES_LOCATION = "webapp";
    static final int DEFAULT_PORT_STOP = 8091;
    static final String STOP_COMMAND = "stop";
    private static final int HTTP_PORT = 8081;
    private static final int HTTPS_PORT = 8082;
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyServer.class);
    private final int startPort;
    private final int stopPort;

    public JettyServer() {
        this(HTTP_PORT, DEFAULT_PORT_STOP);
    }

    public JettyServer(int startPort, int stopPort) {
        this.startPort = startPort;
        this.stopPort = stopPort;
    }

    /**
     * Stops a running web application powered with Jetty.
     * <p/>
     * <p/>
     * Default TCP port is used to communicate with Jetty.
     */
    static public void stop() {
        stop(DEFAULT_PORT_STOP);
    }

    /**
     * Stops a running web application powered with Jetty.
     *
     * @param stopPort TCP port used to communicate with Jetty.
     */
    static public void stop(Integer stopPort) {
        try {
            Socket s = new Socket(InetAddress.getByName("127.0.0.1"), stopPort);
            LOGGER.info("Jetty stopping...");
            s.setSoLinger(false, 0);
            OutputStream out = s.getOutputStream();
            out.write(("stop\r\n").getBytes());
            out.flush();
            s.close();
        } catch (ConnectException e) {
            LOGGER.info("Jetty not running!");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * Start a Jetty web server with its webapp.
     *
     * @param args first argument is the web port, second argument is the port used to stop Jetty
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        JettyServer jettyServer = null;
        if (args.length == 2) {
            jettyServer = new JettyServer(Integer.valueOf(args[0]), Integer.valueOf(args[1]));
        } else {
            jettyServer = new JettyServer();
        }
        jettyServer.start();
    }

    /**
     * Start a Jetty server then deploy a web application.
     *
     * @throws Exception If Jetty fails to start
     */
    public void start() throws Exception {
        Server server = new Server(startPort);
        // init connectors
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(HTTP_PORT);

        HttpConfiguration https = new HttpConfiguration();
        https.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(
                "file:///D:/Lab/jetty-engineio-demo/src/main/resources/keystore.jks");
        sslContextFactory.setKeyStorePassword("admin1234");
        sslContextFactory.setKeyManagerPassword("admin1234");

        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, "http/1.1"),
                new HttpConnectionFactory(https));
        sslConnector.setPort(HTTPS_PORT);

        server.setConnectors(new Connector[]{connector, sslConnector});

        WebAppContext root = new WebAppContext();

        root.setContextPath("/rest-api");
        root.setDescriptor(WEBAPP_RESOURCES_LOCATION + "/WEB-INF/web.xml");

//        URL webAppDir = JettyServer.class.getClassLoader().getResource(WEBAPP_RESOURCES_LOCATION);
        URL webAppDir = new URL("file:///d:/Lab/jetty-engineio-demo/src/main/resources/webapp");
        if (webAppDir == null) {
            throw new RuntimeException(String.format("No %s directory was found into the JAR file", WEBAPP_RESOURCES_LOCATION));
        }
        root.setResourceBase(webAppDir.toURI().toString());
        root.setParentLoaderPriority(true);

        server.setHandler(root);

        server.start();

        LOGGER.info("Jetty server started");
        LOGGER.debug("Jetty web server port: {}", startPort);
        LOGGER.debug("Port to stop Jetty with the 'stop' operation: {}", stopPort);

//        Monitor monitor = new Monitor(stopPort, new Server[]{server});
//        monitor.start();

        server.join();

//        LOGGER.info("Jetty server exited");
    }

}
