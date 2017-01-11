package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by baum: on 08/01/2017.
 */
public class LOGRQ implements Message {
    private String username;

    public String LOGRQ(String username){
        if (!loggedUsers.contains(username)) {
           loggedUsers.add(username);
            this.username = username;
          return ACK.checkACK(0,false);
        }
        else
        return ERROR.getError(7,"");
    }

    @Override
    public short getOpCode() {
        return 7;
    }

}
