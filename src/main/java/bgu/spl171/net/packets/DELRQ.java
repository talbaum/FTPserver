package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by baum on 08/01/2017.
 */
public class DELRQ implements Message {

    public String DelRq ( String filename){
        if(files.contains(filename)){
            for (String file: files)
                if(file.equals(filename)) {
                    files.remove(file);
                    return ACK.checkACK(0,false);
            }
        }
            return ERROR.getError(3,"");
    }

    @Override
    public short getOpCode() {
        return 8;
    }
}
