package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;


/**
 * Created by בbaum on 08/01/2017.
 */
public class DATA implements Message{

    String allTheData="";
       public String Data(byte[] array, int size, int blockNum){
           //we need to keep the data at some way
           allTheData+=array.toString();
       if (size<512) {
        //here we should write the file to Files folder
           return ACK.checkACK(blockNum,true);
       }
       else {
           return ACK.checkACK(blockNum, true);
       }
       }

    @Override
    public short getOpCode() {
        return 3;
    }
}
