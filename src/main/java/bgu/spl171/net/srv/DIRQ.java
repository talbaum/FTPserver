package bgu.spl171.net.srv;
/**
 * Created by baum on 10/01/2017.
 */
public class DIRQ extends Packet{
	public DIRQ(short opcode){
		super(opcode);
	}

	public byte[] encode(){
		byte[] ans=shortToBytes(opcode);
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
		setFinished();
		return null;
	}

}
