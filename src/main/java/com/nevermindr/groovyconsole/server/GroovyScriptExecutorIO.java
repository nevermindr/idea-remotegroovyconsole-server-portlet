package com.nevermindr.groovyconsole.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;

interface GroovyScriptExecutorIO {
    PrintStream getOutputResultMessageStream();

    PrintStream getOutputErrorMessageStream();

    PrintWriter getOutputMessagePrintWriter();

    void reportSuccessMessage(GroovyRemoteRequest groovyRemoteRequest, String msg);

    InputStream getInputStream() throws IOException;

    void reportErrorMessage(GroovyRemoteRequest groovyRemoteRequest, String msg);

    void destroy() throws IOException;
}
