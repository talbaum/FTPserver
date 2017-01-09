package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by baum on 08/01/2017.
 */
public class DIRQ implements Message {
    public String dirq(){
        String allFilesNames="";
        for(String filename :files){
            allFilesNames+= filename + " \0 ";
        }
        if(allFilesNames.equals(""))
            return ERROR.getError(0,"No Files to show");
        else
            return allFilesNames;
    }

    @Override
    public short getOpCode() {
        return 6;
    }
}
