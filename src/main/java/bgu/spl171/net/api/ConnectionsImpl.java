package bgu.spl171.net.api;

import bgu.spl171.net.packets.Packet;
import bgu.spl171.net.srv.BlockingConnectionHandler;
import bgu.spl171.net.srv.ConnectionHandler;
import bgu.spl171.net.srv.NonBlockingConnectionHandler;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by amitu on 07/01/2017.
 */
public class ConnectionsImpl<T> implements bgu.spl171.net.api.Connections<T>{

    Integer nextId=0;
    ConcurrentHashMap<Integer ,ConnectionHandler<T>> MyConnections = new ConcurrentHashMap<Integer ,ConnectionHandler<T>>();
    ConnectionHandler<T> myConHandler;
    TFTPprotocol<T> tp= new TFTPprotocol<T>();

    public void addConnection(ConnectionHandler<T> CH){
        MyConnections.put(nextId,CH);
        //CH.AddAllCon(MyConnections);
        tp.start(nextId,this);
        nextId++;
        myConHandler=CH;
    }

    @Override
    public boolean send(int connectionId, Object msg) {
        if (MyConnections.containsKey(connectionId)) {
            ConnectionHandler<T> tmp= MyConnections.get(connectionId);
            System.out.println(((Packet)msg).isFinished() + " is finished");
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
