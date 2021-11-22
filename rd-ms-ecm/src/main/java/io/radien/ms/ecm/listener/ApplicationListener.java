package io.radien.ms.ecm.listener;

import io.radien.ms.ecm.listener.AbstractApplicationListener;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.servlet.ServletContextEvent;

public class ApplicationListener extends AbstractApplicationListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
    }

    @Override
    protected BeanManager getBeanManager() {
        return CDI.current().getBeanManager();
    }
}
