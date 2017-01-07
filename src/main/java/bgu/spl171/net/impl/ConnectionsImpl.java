package bgu.spl171.net.impl;

import bgu.spl171.net.srv.ConnectionHandler;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by amitu on 07/01/2017.
 */
public class ConnectionsImpl<T> implements bgu.spl171.net.api.Connections<T>{

    ArrayList<ConnectionHandler<T>> MyConnections = new ArrayList<>();

    @Override
    public boolean send(int connectionId, Object msg) {
        if (MyConnections.get(connectionId)!=null) {
            MyConnections.get(connectionId).send((T) msg);
            return true;
        }

        return false;
    }

    @Override
    public void broadcast(Object msg) {
    for (ConnectionHandler<T> Con :MyConnections){
        Con.send((T)msg);
    }
    }

    @Override
    public void disconnect(int connectionId) {
        try {
            MyConnections.get(connectionId).close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
