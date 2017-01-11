package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by baum: on 08/01/2017.
 */
public class ERROR implements Message {

    public static String getError(int errorCode, String errorMsg) {
        switch (errorCode) {
            case 0:
                return 5+ "Not defined, see error message (if any). " + errorMsg;
            case 1:
                return 5+ "File not found – RRQ of non-existing file  " + errorMsg;
            case 2:
                return 5+ "Access violation – File cannot be written, read or deleted.   " + errorMsg;
            case 3:
                return 5+ "Disk full or allocation exceeded – No room in disk " + errorMsg;
            case 4:
                return 5+"Illegal TFTP operation – Unknown Opcode  " + errorMsg;
            case 5:
                return 5+ "File already exists – File name exists on WRQ.  " + errorMsg;
            case 6:
                return 5+"User not logged in – Any opcode received before Login completes.  " + errorMsg;
            case 7:
                return 5 +"User already logged in – Login username already connected.  " + errorMsg;
        }
    return "";
    }


    @Override
    public short getOpCode() {
        return 5;
    }
}
