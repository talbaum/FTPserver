package bgu.spl171.net.impl;

import bgu.spl171.net.impl.srv.ConnectionHandler;
import bgu.spl171.net.impl.api.Connections;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImpl<T> implements Connections<T> {

    Integer nextId=0;
    ConcurrentHashMap<Integer ,ConnectionHandler<T>> MyConnections = new ConcurrentHashMap<Integer ,ConnectionHandler<T>>();
    ConnectionHandler<T> myConHandler;
    TFTPprotocol<T> tp= new TFTPprotocol<T>();

    public void addConnection(ConnectionHandler<T> CH){
        MyConnections.put(nextId,CH);
        tp.start(nextId,this);
        nextId++;
        myConHandler=CH;
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if (MyConnections.containsKey(connectionId)) {
            ConnectionHandler<T> tmp= MyConnections.get(connectionId);
            tmp.send((T) msg);
            return true;
        }

        return false;
    }

    @Override
    public void broadcast(Object msg) {
        Collection<ConnectionHandler<T>> all = MyConnections.values();
            for (ConnectionHandler<T> Con :all){
        Con.send((T)msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        MyConnections.remove(connectionId);
    }
}
