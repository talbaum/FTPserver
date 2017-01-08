package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by בbaum on 08/01/2017.
 */
public class ACK implements Message {
   public static String checkACK (int blockNum, boolean isData){
       if(!isData)
           return "ACK 0";
       else{
           return  "ACK "+blockNum;
       }
   }

    @Override
    public short getOpCode() {
        return 4;
    }
}
