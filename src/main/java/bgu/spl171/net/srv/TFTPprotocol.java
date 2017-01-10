package bgu.spl171.net.srv;

import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.Connections;

/**
 * Created by baum on 10/01/2017.
 */
public class TFTPprotocol<T> implements BidiMessagingProtocol<T> {
    T response=null;

    @Override
    public void start(int connectionId, Connections<T> connections) {

    }

    @Override
    public void process(T message) {
    
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}