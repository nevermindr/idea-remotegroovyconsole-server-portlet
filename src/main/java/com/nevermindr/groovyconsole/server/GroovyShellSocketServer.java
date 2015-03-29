package com.nevermindr.groovyconsole.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GroovyShellSocketServer extends Thread {
    private static final Logger LOGGER = LoggerFactory.getLogger(GroovyShellSocketServer.class);

    private final ServerSocket serverSocket;
    private volatile boolean isRunning = true;
    private ApplicationContext applicationContext;
    private List<GroovyScriptExecutorWorker> workersList = new ArrayList<GroovyScriptExecutorWorker>();

    public GroovyShellSocketServer(int listenPort, String host) throws IOException {
        serverSocket = new ServerSocket(listenPort, 0, InetAddress.getByName(host));
        LOGGER.info("Starting server");
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void stopServer() throws IOException {
        isRunning = false;
        serverSocket.close();
    }

    @Override
    public void run() {
        while (isRunning) {
            try {
                final Socket clientSocket = serverSocket.accept();

                GroovyScriptExecutorWorker groovyScriptExecutorWorker = new GroovyScriptExecutorWorker(clientSocket, applicationContext);
                groovyScriptExecutorWorker.start();

                workersList.add(groovyScriptExecutorWorker);
            } catch (Throwable t) {
                LOGGER.error("Error: " + t);
            }
        }
    }
}
