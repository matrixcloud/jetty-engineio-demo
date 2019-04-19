package io.cubic.jetty.webServer.servlet;

import io.socket.emitter.Emitter;
import io.socket.engineio.parser.Packet;
import io.socket.engineio.server.EngineIoServer;
import io.socket.engineio.server.EngineIoSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by atom on 2019/4/16.
 */
public class SockServlet extends HttpServlet {
    private final EngineIoServer mEngineIoServer = new EngineIoServer();
    private static final Logger LOGGER = LoggerFactory.getLogger(SockServlet.class);

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        LOGGER.info("Sock Servlet Initialized");

        initIO();
    }

    private void initIO() {
        mEngineIoServer.on("connection", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                EngineIoSocket socket = (EngineIoSocket) args[0];
                LOGGER.info("A client connected: ", socket.getId());
                socket.send(new Packet<>(Packet.MESSAGE, "Hi client, I am here"));

                socket.on("message", new Emitter.Listener() {
                    @Override
                    public void call(Object... objects) {
                        String message = (String) objects[0];
                        LOGGER.info("received: " + message);
                    }
                });
            }
        });
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        mEngineIoServer.handleRequest(new HttpServletRequestWrapper(req) {
            @Override
            public boolean isAsyncSupported() {
                return true;
            }
        }, resp);
        mEngineIoServer.handleRequest(req, resp);
    }


    public EngineIoServer getmEngineIoServer() {
        LOGGER.info("getmEngineIoServer");
        return mEngineIoServer;
    }
}
