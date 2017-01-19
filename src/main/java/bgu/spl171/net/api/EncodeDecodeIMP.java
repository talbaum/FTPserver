package bgu.spl171.net.api;

import bgu.spl171.net.packets.*;

public class EncodeDecodeIMP implements MessageEncoderDecoder<Packet> {
    private Packet packet;
    private short opcode;
    private int byteCount = 0;
    private byte[] opArr = new byte[2];

    @Override
    public Packet decodeNextByte(byte nextByte) {

        if(byteCount == 0){
            opArr[0] = nextByte;
            byteCount++;
            System.out.println("0 first char");
        }

        else if(byteCount==1){
            opArr[1] = nextByte;
            byteCount++;
            opcode = bytesToShort(opArr);
            Short s= new Short(opcode);
            System.out.println(s.intValue());

            switch (opcode){
                case((short)1):{
                    packet = new RRQandWRQ(opcode);
                    break;
                }
                case((short)2):{
                    packet = new RRQandWRQ(opcode);
                    break;
                }
                case((short)3):{
                    packet = new DATA(opcode);
                    break;
                }
                case((short)4):{
                    packet = new ACK(opcode);
                    break;
                }
                case((short)5):{
                    packet = new ERROR(opcode);
                    break;
                }
                case((short)6): {
                    packet = new DIRQ(opcode);
                    break;
                }
                case((short)7):{
                    System.out.println("Login User");
                    packet = new LOGRQ(opcode);
                    break;
                }
                case((short)8):{
                    packet = new DELRQ(opcode);
                    break;
                }
                case((short)9):{
                    packet = new BCAST(opcode);
                    break;
                }
                case((short)10): {
                    packet = new DISC(opcode);
                    break;
                }
                default:
                    System.out.println("bad opcode.");
                    break;
            }
        }
        else {
            byteCount++;
            if (packet != null)
                packet.decode(nextByte);
        }

        if(byteCount==2 && ((opcode==(short)6) || opcode==((short)10)))
            packet.setFinished();

        if(packet!=null&&packet.isFinished()) {
            Packet ans=packet;
            packet=null;
            byteCount = 0;
            return ans;
        }
        return packet;
    }


    @Override
    public byte[] encode(Packet message) {
        short myOp=message.getOpcode();
        switch (myOp){
            case((short)1): return ((RRQandWRQ)message).encode();
            case((short)2): return ((RRQandWRQ)message).encode();
            case((short)3): return ((DATA)message).encode();
            case((short)4): return ((ACK)message).encode();
            case((short)5): return ((ERROR)message).encode();
            case((short)6): return ((DIRQ)message).encode();
            case((short)7): return ((LOGRQ)message).encode();
            case((short)8): return ((DELRQ)message).encode();
            case((short)9): return ((BCAST)message).encode();
            case((short)10): return ((DISC)message).encode();
        }
        throw new IllegalArgumentException("illegal opcode recived!");
    }


    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        System.out.println("the result of bytes to short is " + result);
        return result;
    }
}