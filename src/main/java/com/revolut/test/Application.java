package com.revolut.test;

import com.revolut.test.server.JettyServer;

public class Application {

    public static void main(String[] args) throws Exception {
        new JettyServer().start(8080).join();
    }

}
