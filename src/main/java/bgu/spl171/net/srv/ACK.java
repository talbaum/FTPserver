package bgu.spl171.net.srv;
import java.nio.ByteBuffer;
/**
 * Created by baum on 10/01/2017.
 */

public class ACK extends Packet{
	private byte[] arr = new byte[2];
	public short block;
	private int countMyBytesACK;

	public ACK(short opcode) {
		super(opcode);
		block=0;// make sure
        countMyBytesACK=0;//make sure
	}

	public ACK(short opcode, short block) {
		super(opcode);
		this.block=block;
        countMyBytesACK=0; //make sure
	}

	protected byte[] encode(){
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] blockBytes=ByteBuffer.allocate(2).putShort(block).array();
		byte[] ans=new byte[opcodeBytes.length + blockBytes.length+1];
		for (int i=0;i<opcodeBytes.length;i++){
			ans[i]=opcodeBytes[i];
		}
		for (int i=0;i<blockBytes.length;i++){
			ans[i+blockBytes.length]=blockBytes[i];
		}
		ans[ans.length-1]='\0';
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {

		arr[countMyBytesACK]=nextByte;
        countMyBytesACK++;

		if (countMyBytesACK==2){
			block=bytesToShort(arr);
			setFinished();
			return this;
		}
		else
			return null;
	}
}
