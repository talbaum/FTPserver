package bgu.spl171.net.srv;
/**
 * Created by baum on 10/01/2017.
 */
public class DIRQ extends Packet{
	protected DIRQ(short opcode){
		super(opcode);
	}

	protected byte[] encode(){
		byte[] ans=shortToBytes(opcode);
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {		
		setFinished();
		return null;
	}

}
