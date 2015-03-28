package com.nevermindr.groovyconsole.server;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.context.ApplicationContext;

import javax.management.modelmbean.XMLParseException;
import java.io.IOException;

class GroovyScriptExecutorWorker extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptExecutorSocketIO.class);

    private final GroovyScriptExecutorIO io;
    private final ApplicationContext applicationContext;

    private final GroovyRemoteRequest groovyRemoteRequest;

    public GroovyScriptExecutorWorker(GroovyScriptExecutorIO io, ApplicationContext applicationContext) {
        this.groovyRemoteRequest = new GroovyRemoteRequest();
        this.applicationContext = applicationContext;
        this.io = io;
    }

    @Override
    public void run() {
        try {
            try {
                groovyRemoteRequest.parseInputData(io.getInputStream());
            } catch (IOException ex) {
                LOGGER.error("Error: Unable to read XML from client!\n\t" + ex);
                throw ex;
            } catch (NullPointerException ex) {
                LOGGER.error("Error: Unable to parse XML from client!\n\t" + ex);
                throw new XMLParseException(ex.getMessage());
            }

            final Binding bindings = new Binding();
            initializeBindings(bindings);
            final GroovyShell sh = new GroovyShell(bindings);

            long startTime = System.currentTimeMillis();
            sh.run(groovyRemoteRequest.fullScript, "someScript.groovy", new String[]{});
            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;

            io.reportSuccessMessage(groovyRemoteRequest, String.format("Executed in %dms", elapsedTime));
        } catch (Exception e) {
            io.reportErrorMessage(groovyRemoteRequest, String.format("Exception has occured: %s", e));
        } finally {
            try {
                io.destroy();
            } catch (IOException e) {
                LOGGER.error(String.format("Error closing client socket: %s", e));
            }
        }
    }

    private void initializeBindings(Binding bindings) {
        bindings.setVariable("context", applicationContext);
        for (String name : applicationContext.getBeanDefinitionNames()) {
            try {
                bindings.setVariable(name, applicationContext.getBean(name));
            } catch (BeanIsAbstractException e) {
                // skip
            } catch (Throwable t) {
                LOGGER.warn("Can't get bean " + name + ", error: " + t, t);
            }
        }

        bindings.setProperty("out", io.getOutputResultMessageStream());
        bindings.setProperty("err", io.getOutputErrorMessageStream());
    }

}
