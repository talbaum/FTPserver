package bgu.spl171.net.impl.TFTPtcp.packets;
/**
 * Created by baum on 10/01/2017.
 */
public class DISC extends Packet{
	public DISC(short opcode) {
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
