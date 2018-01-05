package com.meti.connect;

import com.meti.io.Source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/5/2018
 */
public class ObjectConnection extends Connection {
    private final ObjectInputStream inputStream;
    private final ObjectOutputStream outputStream;

    public ObjectConnection(Connection connection) throws IOException {
        this(connection.getSource());
    }

    public ObjectConnection(Source source) throws IOException {
        super(source);
        //output stream before input stream because of header
        if (source.getOutputStream() instanceof ObjectOutputStream) {
            outputStream = (ObjectOutputStream) source.getOutputStream();
        } else {
            outputStream = new ObjectOutputStream(source.getOutputStream());
        }

        if (source.getInputStream() instanceof ObjectInputStream) {
            inputStream = (ObjectInputStream) source.getInputStream();
        } else {
            inputStream = new ObjectInputStream(source.getInputStream());
        }
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
        outputStream.close();

        //close parent streams?
    }

    public Object readObject() throws IOException, ClassNotFoundException {
        return inputStream.readObject();
    }

    public Object readUnshared() throws IOException, ClassNotFoundException {
        return inputStream.readUnshared();
    }

    public void writeObject(Object obj) throws IOException {
        outputStream.writeObject(obj);
    }

    public void writeUnshared(Object obj) throws IOException {
        outputStream.writeUnshared(obj);
    }
}
