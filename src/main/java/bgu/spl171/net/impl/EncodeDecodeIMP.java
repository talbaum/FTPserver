package bgu.spl171.net.impl;

import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.packets.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Created by amitu on 08/01/2017.
 */
public class EncodeDecodeIMP implements MessageEncoderDecoder {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;
    short OPcode=0;
    boolean hasOPcode=false;
    String ans;
    //for Data use
    boolean issize=false;
    int size=0;
    boolean hasBlock=false;
    int dataBlock=0;

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    @Override
    public String decodeNextByte(byte nextByte) {

        switch (OPcode){
            case 0:
                pushByte(nextByte);
                return null; //not a line yet
                break;

            case 1:
                RRQ tmp1 = new RRQ();
                if (nextByte==0){
                    ans=tmp1.read(popString());
                }
                else
                    pushByte(nextByte);
                return null;
                break;

            case 2:
                WRQ tmp2 = new WRQ();
                if (nextByte==0){
                    ans=tmp2.write(popString());
                }
                else
                    pushByte(nextByte);
                return null;
            break;

            case 3:

                pushByte(nextByte);
                if (!issize & len==2){
                    issize=true;
                    size=Integer.parseInt(popString());
                }
                if (!hasBlock & len==2){
                    hasBlock=true;
                    dataBlock=Integer.parseInt(popString());
                }
                if (len==size) {
                    DATA tmp3 = new DATA();
                    ans=tmp3.Data(Arrays.copyOfRange(bytes, 0, len - 1), size, dataBlock);
                    OPcode = 0;
                    size = 0;
                    hasOPcode = false;
                    issize = false;
                    hasBlock = false;
                    dataBlock = 0;
                }
                    break;

            case 6:
                DIR tmp6 = new DIR();
                ans=tmp6.Dirq();

            case 7:
                LOGRQ tmp7 = new LOGRQ();
                if (nextByte==0){
                    ans=tmp7.LOGRQ(popString());
                }
                else
                    pushByte(nextByte);
                return null;
            break;

            case 8:
                DELRQ tmp8 = new DELRQ();
                if (nextByte==0){
                    ans=tmp8.DelRq(popString());
                }
                else
                    pushByte(nextByte);
                return null;
            break;


            case 10:
                Dis();


        }

        if (OPcode!=(0|1|2|3|6|7|8|10)){
        String ans = ERROR.getError(4,"");
        }

        //notice that the top 128 ascii characters have the same representation as their utf-8 counterparts
        //this allow us to do the following comparison

        if (nextByte == '\n') {
            return popString();
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
        return result;
    }
}
