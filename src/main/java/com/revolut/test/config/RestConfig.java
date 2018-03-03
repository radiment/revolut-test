package com.revolut.test.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.server.ResourceConfig;
import org.jvnet.hk2.guice.bridge.api.GuiceBridge;
import org.jvnet.hk2.guice.bridge.api.GuiceIntoHK2Bridge;

import javax.inject.Inject;

public class RestConfig extends ResourceConfig {

    @Inject
    public RestConfig(ServiceLocator locator) {
        packages("com.revolut.test.rest");

        // Instantiate Guice Bridge
        GuiceBridge.getGuiceBridge().initializeGuiceBridge(locator);

        GuiceIntoHK2Bridge guiceBridge = locator.getService(GuiceIntoHK2Bridge.class);
        Injector injector = Guice.createInjector(new DIConfig());
        guiceBridge.bridgeGuiceInjector(injector);
    }
}
