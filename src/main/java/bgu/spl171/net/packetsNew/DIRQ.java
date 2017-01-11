package bgu.spl171.net.packetsNew;

public class DIRQ extends Packet{

	protected DIRQ(short Opcode){
		this.Opcode = Opcode;
	}
	
	protected byte[] encode(){
		
		byte[] ans = shortToBytes(Opcode);
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {		
		return null;
	}

}
