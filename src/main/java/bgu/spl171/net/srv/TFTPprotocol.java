package bgu.spl171.net.srv;

import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.Connections;

/**
 * Created by baum on 10/01/2017.
 */
public class TFTPprotocol<T> implements BidiMessagingProtocol<T> {
    //T response=null;
    int ID;
    ConnectionsImpl Connections1;
    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.Connections1=(ConnectionsImpl)connections;
        this.ID=connectionId;
    }

    @Override
    public void process(T message) {
    Connections1.send(ID,message);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}