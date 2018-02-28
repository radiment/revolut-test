package com.revolut.test;

import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

public class Application {

    public static void main(String[] args) throws Exception {
        Server server = new Server();

        ServerConnector connector = new ServerConnector(server);
        connector.setPort(8080);
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
        holder.setInitParameter("com.sun.jersey.config.property.packages", "com.revolut.test.rest");
        holder.setInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
        return holder;
    }

}
