package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by באום on 08/01/2017.
 */
public class WRQ implements Message {
    //need to write the file itself to files directory?

   public String write(String filename){
       files.add(filename);
       return ACK.checkACK(0,false);
   }

    @Override
    public short getOpCode() {
        return 2;
    }
}
