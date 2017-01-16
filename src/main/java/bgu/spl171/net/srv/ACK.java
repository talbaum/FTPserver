package bgu.spl171.net.srv;

import java.nio.ByteBuffer;

public class ACK extends Packet{
	public short block;
	private int byteCount;
	private byte[] arr = new byte[2];

	public ACK(short opcode) {
		super(opcode);
		block=0;// make sure
		byteCount=0;//make sure
	}

	public ACK(short opcode, short block) {
		super(opcode);
		this.block=block;
		byteCount=0; //make sure
	}

	protected byte[] encode(){
		byte[] opcodeBytes = shortToBytes(opcode);
		byte[] blockBytes = ByteBuffer.allocate(2).putShort(block).array();
		byte[] ans = new byte[opcodeBytes.length + blockBytes.length];
		
		for (int i=0; i<opcodeBytes.length; i++){
			ans[i]=opcodeBytes[i];
		}
		
		for (int i=0; i<blockBytes.length; i++){
			ans[i+blockBytes.length]=blockBytes[i];
		}
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		arr[this.byteCount] = nextByte;
		this.byteCount++;

		if (this.byteCount == 2){
			block = bytesToShort(arr);
			setFinished();
			return this;
		}
		else
			return null;
	}
}
