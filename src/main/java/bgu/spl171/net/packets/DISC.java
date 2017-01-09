package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by baum on 08/01/2017.
 */
public class DISC implements Message {
   public String disconnect(String username){
        for(String user:loggedUsers){
            if(user.equals(username)) {
                loggedUsers.remove(username);
                return ACK.checkACK(0,false);
            }
            }
       return ERROR.getError(6,"");
   }
    @Override
    public short getOpCode() {
        return 10;
    }
}
