package bgu.spl171.net.srv;

public class DISC extends Packet{


	public DISC(short opcode) {
		super(opcode);
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
