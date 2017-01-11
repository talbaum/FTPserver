package bgu.spl171.net.srv;

import bgu.spl171.net.api.Message;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.packets.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by amitu on 08/01/2017.
 */
public class EncodeDecodeIMP<T> implements MessageEncoderDecoder {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    boolean isLogged=false;
    short OPcode=0;
    boolean hasOPcode=false;
    String ans;
    //for Data use
    boolean issize=false;
    int size=0;
    boolean hasBlock=false;
    int dataBlock=0;
    String username="";

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    @Override
    public byte[] decodeNextByte(byte nextByte) {

        switch (OPcode) {
            case 0:
                pushByte(nextByte);
                return null; //not a line yet

            case 1:
                if (isLogged) {
                    RRQ tmp1 = new RRQ();
                    if (nextByte == 0) {
                        ans = tmp1.read(popString());
                        return encode(ans);
                    } else
                        pushByte(nextByte);
                    return null;
                } else
                    return encode(ERROR.getError(6, ""));

            case 2:
                if (isLogged) {
                    WRQ tmp2 = new WRQ();
                    if (nextByte == 0) {
                        ans = tmp2.write(popString());
                        return encode(ans);
                    } else
                        pushByte(nextByte);
                    return null;
                } else
                    return encode(ERROR.getError(6, ""));

            case 3:
                if (isLogged) {
                    pushByte(nextByte);
                    if (!issize & len == 2) {
                        issize = true;
                        size = Integer.parseInt(popString());
                    }
                    if (!hasBlock & len == 2) {
                        hasBlock = true;
                        dataBlock = Integer.parseInt(popString());
                    }
                    if (len == size) {
                        DATA tmp3 = new DATA();
                        ans = tmp3.Data(Arrays.copyOfRange(bytes, 0, len - 1), size, dataBlock);
                        OPcode = 0;
                        size = 0;
                        hasOPcode = false;
                        issize = false;
                        hasBlock = false;
                        dataBlock = 0;
                        return encode(ans);
                    }
                } else
                    return encode(ERROR.getError(6, ""));
                break;

            case 6:
                if (isLogged) {
                    DIRQ tmp6 = new DIRQ();
                    if (nextByte == 0) {
                        ans = tmp6.dirq();
                        return encode(ans);
                    } else {
                        pushByte(nextByte);
                        return null;
                    }
                } else
                    return encode(ERROR.getError(6, ""));

            case 7:
                    LOGRQ tmp7 = new LOGRQ();
                    if (nextByte == 0) {
                        username = popString();
                        ans = tmp7.LOGRQ(username);
                        if (ans.equals("ACK 0"))
                            isLogged = true;

                        return encode(ans);
                    } else
                        pushByte(nextByte);
                    return null;

            case 8:
                if (isLogged) {
                    DELRQ tmp8 = new DELRQ();
                    if (nextByte == 0) {
                        ans = tmp8.DelRq(popString());
                        return encode(ans);
                    } else {
                        pushByte(nextByte);
                        return null;
                    }
                } else
                    return encode(ERROR.getError(6, ""));

            case 9:
                if (isLogged) {
                    //1bcast
                } else
                    return encode(ERROR.getError(6, ""));
                break;

            case 10:
                if (isLogged) {
                    DISC d = new DISC();
                    if (nextByte == 0) {
                        ans = d.disconnect(username);
                        if (ans.equals("ACK 0")) {
                            isLogged = false;
                            username="";
                        }
                        return encode(ans);
                    } else {
                        pushByte(nextByte);
                        return null;
                    }
                } else
                    return encode(ERROR.getError(6, ""));
        }

        if (OPcode!=(0|1|2|3|6|7|8|10)){
        String ans = ERROR.getError(4,"");
        return ans.getBytes();
        }

        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison

        if (nextByte == '\n') {
            return popString().getBytes();
        }

        pushByte(nextByte);
        return null; //not a line yet
    }



    @Override
    public byte[] encode(Object message){
    return (message + "\n").getBytes(); //uses utf8 by default
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        if ((!hasOPcode)&(len==2)){
            this.OPcode = bytesToShort(bytes);
            hasOPcode=true;
            len=0;
        }
        bytes[len++] = nextByte;
    }

    private String popString() {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        OPcode=0;
        hasBlock=false;
        hasOPcode=false;
        return result;
    }
}
