package com.meti.connect;

import com.meti.io.Source;

public class SimpleConnection extends Connection<Source<?, ?>> {
    public SimpleConnection(Source<?, ?> source) {
        super(source);
    }
}
