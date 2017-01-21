package bgu.spl171.net.api;

import bgu.spl171.net.packets.*;

public class EncodeDecodeIMP implements MessageEncoderDecoder<Packet> {
    private Packet packet;
    private short opcode;
    private int byteCount = 0;
    private byte[] opArr = new byte[2];

    @Override
    public Packet decodeNextByte(byte nextByte) {
        System.out.println("decodeNextByte");
        if(byteCount == 0){
            opArr[0] = nextByte;
            byteCount++;
        }

        else if(byteCount==1){
            opArr[1] = nextByte;
            byteCount++;
            opcode = bytesToShort(opArr);
            Short s= new Short(opcode);


            switch (opcode){
                case((short)0):
                    System.out.println("opcode 0?");
                    break;
                case((short)1):{
                    System.out.println("RRQ Creation");
                    packet = new RRQandWRQ(opcode);
                    break;
                }
                case((short)2):{
                    System.out.println("WRQ Creation");
                    packet = new RRQandWRQ(opcode);
                    break;
                }
                case((short)3):{
                    System.out.println("Data Creation");
                    packet = new DATA(opcode);
                    break;
                }
                case((short)4):{
                    System.out.println("ACK Creation");
                    packet = new ACK(opcode);
                    break;
                }
                case((short)5):{
                    System.out.println("Error Creation");
                    packet = new ERROR(opcode);
                    break;
                }
                case((short)6): {
                    System.out.println("Dirq Creation");
                    packet = new DIRQ(opcode);
                    break;
                }
                case((short)7):{
                    System.out.println("Login User Creation");
                    packet = new LOGRQ(opcode);
                    break;
                }
                case((short)8):{
                    System.out.println("Delrq Creation");
                    packet = new DELRQ(opcode);
                    break;
                }
                case((short)9):{
                    System.out.println("BCAST Creation");
                    packet = new BCAST(opcode);
                    break;
                }
                case((short)10): {
                    System.out.println("Discconcet User Creation");
                    packet = new DISC(opcode);
                    break;
                }
            }
        }
        else {
            byteCount++;
            if (packet != null)
                packet.decode(nextByte);
        }

        if(byteCount==2 && ((opcode==(short)6) || opcode==((short)10)))
        {
            packet.decode(nextByte);
            packet.setFinished();
        }

        if(packet!=null&&packet.isFinished()) {
            Packet ans=packet;
            packet=null;
            System.out.println(byteCount + " is all the bytes i have in this package");
            byteCount = 0;
            return ans;
        }
        return packet;
    }


    @Override
    public  byte[] encode(Packet message) {
        short myOp=message.getOpcode();
        System.out.println(myOp+ " is the encoded opcode (4 is ACK! 7 is LOGRQ!d)");
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
        return result;
    }
}