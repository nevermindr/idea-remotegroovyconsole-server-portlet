package com.nevermindr.groovyconsole.server;

import com.nevermindr.groovyconsole.server.helper.MessageFilterOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class GroovyScriptExecutorSocketIO implements GroovyScriptExecutorIO {
    // --Commented out by Inspection (28.03.2015 01:06):private static final Logger LOGGER = LoggerFactory.getLogger(GroovyScriptExecutorSocketIO.class);
    private static final String END_LINE_OUTPUT_SUFFIX = "<<<OUTPUT";
    private static final String END_LINE_ERROR_SUFFIX = "<<<ERROR";
    private static final String encoding = System.getProperty("file.encoding");
    private final Socket clientSocket;
    private PrintStream outputResultMessageStream;
    private PrintStream outputErrorMessageStream;
    private PrintWriter outputMessagePrintWriter;

    public GroovyScriptExecutorSocketIO(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;

        this.outputResultMessageStream = initOutputMessageStream(clientSocket, END_LINE_OUTPUT_SUFFIX);
        this.outputErrorMessageStream = initOutputMessageStream(clientSocket, END_LINE_ERROR_SUFFIX);

        this.outputMessagePrintWriter = new PrintWriter(clientSocket.getOutputStream());
    }

    private PrintStream initOutputMessageStream(Socket s, String suffix) throws IOException {
        return new PrintStream(new MessageFilterOutputStream(s.getOutputStream(), suffix), true, encoding);
    }

    @Override
    public PrintStream getOutputResultMessageStream() {
        return outputResultMessageStream;
    }

    @Override
    public PrintStream getOutputErrorMessageStream() {
        return outputErrorMessageStream;
    }

    @Override
    public PrintWriter getOutputMessagePrintWriter() {
        return outputMessagePrintWriter;
    }

    @Override
    public void reportSuccessMessage(GroovyRemoteRequest groovyRemoteRequest, String msg) {
        outputMessagePrintWriter.println(groovyRemoteRequest.returnSeparator);
        outputMessagePrintWriter.println(msg);
        outputMessagePrintWriter.flush();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return clientSocket.getInputStream();
    }

    @Override
    public void reportErrorMessage(GroovyRemoteRequest groovyRemoteRequest, String msg) {
        if (groovyRemoteRequest.errorSeparator == null) { //Request XML failed to parse
            outputErrorMessageStream.println(msg);
            outputErrorMessageStream.flush();
        } else {
            outputMessagePrintWriter.println(groovyRemoteRequest.errorSeparator);
            outputMessagePrintWriter.println(msg);
            outputMessagePrintWriter.flush();
        }
    }

    @Override
    public void destroy() throws IOException {
        this.outputResultMessageStream.close();
        this.outputErrorMessageStream.close();
        this.outputMessagePrintWriter.close();
        this.clientSocket.close();
    }
}