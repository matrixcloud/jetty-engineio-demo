package com.javaetmoi.jetty.singleServer;

import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.JettyWebSocketHandler;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

final class LongPollingNotOk {

    private static final AtomicInteger PORT_START = new AtomicInteger(3000);

    private int mPort;
    private Server mServer;
    private EngineIoServer mEngineIoServer;

    static {
        Log.setLog(new JettyNoLogging());
    }

    LongPollingNotOk() {
        mPort = PORT_START.getAndIncrement();
        mServer = new Server(8081);
        mEngineIoServer = new EngineIoServer();

        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletContextHandler.setContextPath("/rest-api");

        servletContextHandler.addServlet(new ServletHolder(new HttpServlet() {

            @Override
            public void init() throws ServletException {
                super.init();
                System.out.println("--init--");
            }

            @Override
            protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
                mEngineIoServer.handleRequest(new HttpServletRequestWrapper(request) {
                    @Override
                    public boolean isAsyncSupported() {
                        return false; // if you want the long-polling to make a effect, you need let it return true
                    }
                }, response);
            }
        }), "/sock/*");

        try {
            WebSocketUpgradeFilter webSocketUpgradeFilter = WebSocketUpgradeFilter.configureContext(servletContextHandler);
            webSocketUpgradeFilter.addMapping(
                    new ServletPathSpec("/sock/*"),
                    (servletUpgradeRequest, servletUpgradeResponse) -> new JettyWebSocketHandler(mEngineIoServer));
        } catch (ServletException ex) {
            ex.printStackTrace();
        }

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(new Handler[] { servletContextHandler });
        mServer.setHandler(handlerList);
    }

    void startServer() throws Exception {
        mServer.start();
    }

    void stopServer() throws Exception {
        mServer.stop();
    }

    int getPort() {
        return mPort;
    }

    EngineIoServer getEngineIoServer() {
        return mEngineIoServer;
    }

    private static final class JettyNoLogging implements Logger {

        @Override public String getName() {
            return "no";
        }

        @Override
        public void warn(String s, Object... objects) {
        }

        @Override
        public void warn(Throwable throwable) {
        }

        @Override
        public void warn(String s, Throwable throwable) {
        }

        @Override
        public void info(String s, Object... objects) {
        }

        @Override
        public void info(Throwable throwable) {
        }

        @Override
        public void info(String s, Throwable throwable) {
        }

        @Override
        public boolean isDebugEnabled() {
            return false;
        }

        @Override
        public void setDebugEnabled(boolean b) {
        }

        @Override
        public void debug(String s, Object... objects) {
        }

        @Override
        public void debug(String s, long l) {
        }

        @Override
        public void debug(Throwable throwable) {
        }

        @Override
        public void debug(String s, Throwable throwable) {
        }

        @Override
        public Logger getLogger(String s) {
            return this;
        }

        @Override
        public void ignore(Throwable throwable) {
        }
    }

    public static void main(String[] args) throws Exception {
        new LongPollingNotOk().startServer();
    }
}
