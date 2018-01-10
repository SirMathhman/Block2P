package com.meti.io.connect;

import com.meti.io.Peer;
import com.meti.io.connect.connections.Connection;
import com.meti.util.handle.Handler;

/**
 * Pseudo-implementation of Handler that specifies a strict implementation for a Peer.
 * @see Peer
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public abstract class ConnectionHandler extends Handler<Connection, Object> {
}
