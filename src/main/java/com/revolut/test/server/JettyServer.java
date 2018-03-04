package com.revolut.test.server;

import com.revolut.test.config.RestConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class JettyServer {

    public void start(int port) throws Exception {
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");
        context.addServlet(getRestServlet(), "/*");

        server.setHandler(context);
        server.start();
    }

    private static ServletHolder getRestServlet() {
        ServletHolder holder = new ServletHolder();
        holder.setServlet(new ServletContainer());
        holder.setInitParameter("javax.ws.rs.Application", RestConfig.class.getName());
        return holder;
    }
}
