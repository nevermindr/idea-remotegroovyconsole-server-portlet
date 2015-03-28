package com.nevermindr.groovyconsole.server.helper;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MessageFilterOutputStream extends FilterOutputStream {
    private final String endLineSuffix;
    private char previousChar;

    public MessageFilterOutputStream(OutputStream out, String endLineSuffix) {
        super(out);
        this.endLineSuffix = endLineSuffix;
    }

    public void write(int b) throws IOException {
        if (b == '\n') {
            super.write(endLineSuffix.getBytes(StandardCharsets.UTF_8));
            if (previousChar == '\r') {
                super.write((int) (char) '\r');
            }
            super.write((int) (char) '\n');
        } else {
            if (b != '\r') {
                super.write(b);
            }
            previousChar = (char) b;
        }
    }


}
