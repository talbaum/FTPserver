package bgu.spl171.net.impl.TFTPtcp;

import bgu.spl171.net.impl.TFTPtcp.srv.ConnectionHandler;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by amitu on 07/01/2017.
 */
public class ConnectionsImpl<T> implements bgu.spl171.net.impl.TFTPtcp.api.Connections<T>{

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