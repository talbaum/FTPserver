package bgu.spl171.net.packets;
import bgu.spl171.net.api.Message;


/**
 * Created by baum on 08/01/2017.
 */
public class RRQ implements Message {
    //need to read the file itself from file directory?

    public String read (String filename){
        if(files.contains(filename))
            return ACK.checkACK(0,false);
        else
            return ERROR.getError(1,"");
    }

    @Override
    public short getOpCode() {
        return 1;
    }
}