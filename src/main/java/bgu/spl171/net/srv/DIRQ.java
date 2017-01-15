package bgu.spl171.net.srv;

public class DIRQ extends Packet{

	protected DIRQ(short opcode){
		super(opcode);
	}
	
	protected byte[] encode(){
		
		byte[] ans = shortToBytes(Opcode);
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {		
		setFinished();
		return null;
	}

}
