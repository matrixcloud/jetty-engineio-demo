package com.javaetmoi.jetty.singleServer;

import com.javaetmoi.jetty.sock.SockServlet;
import io.socket.engineio.server.JettyWebSocketHandler;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;

import javax.servlet.ServletException;

/**
 * Created by atom on 2019/4/16.
 */
public class IOServer {
    private Server mServer;

    void start() throws Exception {
//        mServer = new Server(8082);
//
//        ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//        servletContextHandler.setContextPath("/");
//
//        final SockServlet servlet = new SockServlet();
//        servletContextHandler.addServlet(new ServletHolder(servlet), "/*");
//
//        try {
//            WebSocketUpgradeFilter webSocketUpgradeFilter = WebSocketUpgradeFilter.configureContext(servletContextHandler);
//            webSocketUpgradeFilter.addMapping(
//                    new ServletPathSpec("/*"),
//                    (servletUpgradeRequest, servletUpgradeResponse) -> new JettyWebSocketHandler(servlet.getmEngineIoServer()));
//        } catch (ServletException ex) {
//            ex.printStackTrace();
//        }
//
//        HandlerList handlerList = new HandlerList();
//        handlerList.setHandlers(new Handler[] { servletContextHandler });
//        mServer.setHandler(handlerList);
//
//        mServer.start();
//        mServer.join();
        new ServerWrapper().startServer();
    }

    public static void main(String[] args) throws Exception {
        new Server().start();
    }
}
