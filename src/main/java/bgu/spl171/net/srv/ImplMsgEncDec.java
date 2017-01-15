package bgu.spl171.net.srv;

public class ImplMsgEncDec implements MessageEncoderDecoder<Packet>{
	private int byteCounter = 0;
	private byte[] findOpcode = new byte[2];
	private short Opcode;
	private Packet packet;
	
	@Override
	public Packet decodeNextByte(byte nextByte) {

		//------------- המצב שעוד לא יודעים איזה פאקט זה -------------
		if(byteCounter == 0){
			findOpcode[0] = nextByte;
			byteCounter++;
		//	return null;
		}
		
		//------------- יצירת האופקוד / שליחת הבייט הנוכחי ל... -------------
		else if(byteCounter < 2){
			findOpcode[1] = nextByte;
			byteCounter++;
			Opcode = bytesToShort(findOpcode);
			switch (Opcode){
				case((short)1):{
					packet = new RRQandWRQ(Opcode);
					break;
				}
				case((short)2):{
					packet = new RRQandWRQ(Opcode);
					break;
				}
				case((short)3):{
					packet = new DATA(Opcode);
					break;
				}
				case((short)4):{
					packet = new ACK(Opcode);
					break;
				}
				case((short)5):{
					packet = new ERROR(Opcode);
					break;
				}
				case((short)6): {
					packet = new DIRQ(Opcode);
					break;
				}
				case((short)7):{
					packet = new LOGRQ(Opcode);
					break;
				}
				case((short)8):{
					packet = new DELRQ(Opcode);
					break;
				}
				case((short)9):{
					packet = new BCAST(Opcode);
					break;
				}
				case((short)10): {
					packet = new DISC(Opcode);
					break;
				}
			}
			//return packet;
		}
		else {
			byteCounter++;
			if (packet != null)
				packet.decode(nextByte);
			}

		if(byteCounter==2 && ((Opcode==(short)6) || Opcode==((short)10)))
			packet.setFinished();

		if(packet!=null&&packet.isFinished()) {
			Packet ans=packet;
			packet=null;
				byteCounter = 0;
				return ans;
		}
		return packet;
	}
	

	@Override
	public byte[] encode(Packet message) {
		switch (message.getOpcode()){
			case(1): return ((RRQandWRQ)message).encode();
			case(2): return ((RRQandWRQ)message).encode();
			case(3): return ((DATA)message).encode();
			case(4): return ((ACK)message).encode();
 			case(5): return ((ERROR)message).encode();
			case(6): return ((DIRQ)message).encode();
			case(7): return ((LOGRQ)message).encode();
			case(8): return ((DELRQ)message).encode();
			case(9): return ((BCAST)message).encode();
			case(10): return ((DISC)message).encode();
		}	
		throw new IllegalArgumentException("unvalid packet Opcode");
	}
	
	public short bytesToShort(byte[] byteArr)
	{
	    short result = (short)((byteArr[0] & 0xff) << 8);
	    result += (short)(byteArr[1] & 0xff);
	    return result;
	}

}
