package bgu.spl171.net.api;

import bgu.spl171.net.srv.ConnectionHandler;
import bgu.spl171.net.srv.NonBlockingConnectionHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by amitu on 07/01/2017.
 */
public class ConnectionsImpl<T> implements bgu.spl171.net.api.Connections<T>{

    Integer nextId=0;
    ConcurrentHashMap<Integer ,ConnectionHandler<T>> MyConnections = new ConcurrentHashMap<Integer ,ConnectionHandler<T>>();

    public void addConnection(NonBlockingConnectionHandler<T> CH){
        MyConnections.put(nextId++,CH);
        //CH.AddAllCon(MyConnections);
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if (MyConnections.containsKey(connectionId)) {
            MyConnections.get(connectionId).send((T) msg);
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
        try {
            MyConnections.get(connectionId).close();
            MyConnections.remove(connectionId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
