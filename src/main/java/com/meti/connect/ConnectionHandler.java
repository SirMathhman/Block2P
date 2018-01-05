package com.meti.connect;

import com.meti.connect.connections.Connection;
import com.meti.util.Handler;

/**
 * Pseudo-implementation of Handler that specifies a strict implementation for a Peer.
 * @see com.meti.Peer
 * @author SirMathhman
 * @version 0.0.0
 * @since 1/2/2018
 */
public abstract class ConnectionHandler extends Handler<Connection, Boolean> {
}
