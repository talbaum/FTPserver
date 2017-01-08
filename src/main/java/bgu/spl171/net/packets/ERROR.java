package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

/**
 * Created by baum: on 08/01/2017.
 */
public class ERROR implements Message {

    public static String getError(int errorCode, String errorMsg) {
        switch (errorCode) {
            case 0:
                return " Not defined, see error message (if any). " + errorMsg;
            case 1:
                return "  File not found – RRQ of non-existing file  " + errorMsg;
            case 2:
                return "  Access violation – File cannot be written, read or deleted.   " + errorMsg;
            case 3:
                return "  Disk full or allocation exceeded – No room in disk " + errorMsg;
            case 4:
                return "   Illegal TFTP operation – Unknown Opcode  " + errorMsg;
            case 5:
                return "  File already exists – File name exists on WRQ.  " + errorMsg;
            case 6:
                return "   User not logged in – Any opcode received before Login completes.  " + errorMsg;
            case 7:
                return "    User already logged in – Login username already connected.  " + errorMsg;
        }
    return "";
    }


    @Override
    public short getOpCode() {
        return 5;
    }
}
