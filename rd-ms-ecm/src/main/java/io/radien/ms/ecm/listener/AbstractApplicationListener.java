package io.radien.ms.ecm.listener;

import io.radien.ms.ecm.event.ApplicationInitializedEvent;

import javax.enterprise.inject.spi.BeanManager;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public abstract class AbstractApplicationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        BeanManager bm = getBeanManager();
        if (bm != null) {
            bm.fireEvent(new ApplicationInitializedEvent());
        }
    }

    protected abstract BeanManager getBeanManager();
}
