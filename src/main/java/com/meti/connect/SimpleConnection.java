package com.meti.connect;

import java.io.InputStream;
import java.io.OutputStream;

public class SimpleConnection extends Connection<InputStream, OutputStream> {
    public SimpleConnection(InputStream inputStream, OutputStream outputStream) {
        super(inputStream, outputStream);
    }
}
