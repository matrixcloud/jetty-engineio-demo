package io.cubic.jetty.webServer;

import io.cubic.jetty.webServer.servlet.SockServlet;
import io.socket.engineio.server.JettyWebSocketHandler;
import org.eclipse.jetty.http.pathmap.ServletPathSpec;
import org.eclipse.jetty.websocket.server.WebSocketUpgradeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;

/**
 * Created by atom on 2019/4/17.
 */
public class StartupListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.info("-- contextInitialized --");
        try {
            ServletContext context = servletContextEvent.getServletContext();
            SockServlet sockServlet = new SockServlet();
            ServletRegistration.Dynamic dynamic = context.addServlet("SockServlet", sockServlet);
            dynamic.addMapping("/sock/*");
            dynamic.setLoadOnStartup(1);

            WebSocketUpgradeFilter filter = WebSocketUpgradeFilter.configureContext(context);
            filter.addMapping(new ServletPathSpec("/sock/*"), ((servletUpgradeRequest, servletUpgradeResponse) -> new JettyWebSocketHandler(sockServlet.getmEngineIoServer())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
