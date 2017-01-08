package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by באום on 08/01/2017.
 */
public class WRQ implements Message {
   public String write(String filename){
       files.add(filename);
       return getOpCode() + "" + filename;
   }

    @Override
    public short getOpCode() {
        return 2;
    }
}
