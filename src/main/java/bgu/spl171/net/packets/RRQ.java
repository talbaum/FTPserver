package bgu.spl171.net.packets;
import bgu.spl171.net.api.Message;


/**
 * Created by baum on 08/01/2017.
 */
public class RRQ implements Message {
    public String read (String filename){
        if(files.contains(filename))
            return getOpCode() +""+ filename;
        else
            return ERROR.getError(2,"");
    }

    @Override
    public short getOpCode() {
        return 1;
    }
}