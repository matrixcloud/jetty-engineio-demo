package io.cubic.jetty.webServer.sock;

public class Stop {

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            JettyServer.stop(Integer.valueOf(args[0]));
        } else {
            JettyServer.stop();
        }

    }
}
