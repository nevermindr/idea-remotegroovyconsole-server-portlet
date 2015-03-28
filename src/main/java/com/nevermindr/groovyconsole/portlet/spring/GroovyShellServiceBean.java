package com.nevermindr.groovyconsole.portlet.spring;

import com.nevermindr.groovyconsole.server.GroovyShellSocketServer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class GroovyShellServiceBean implements InitializingBean, DisposableBean, ApplicationContextAware {

    private GroovyShellSocketServer service;
    private ApplicationContext applicationContext;
    private boolean launchAtStart = true;
    private int port;
    private String host;

    @SuppressWarnings("unused")
    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @SuppressWarnings("unused")
    public boolean isLaunchAtStart() {
        return launchAtStart;
    }

    public void setLaunchAtStart(boolean launchAtStart) {
        this.launchAtStart = launchAtStart;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void destroy() throws Exception {
        service.stopServer();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        service = new GroovyShellSocketServer(port, host);
        service.setApplicationContext(applicationContext);
        if (launchAtStart) {
            service.start();
        }
    }


}
