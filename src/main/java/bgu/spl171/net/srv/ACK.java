package bgu.spl171.net.srv;

import java.nio.ByteBuffer;

public class ACK extends Packet{
	public int block; //int?
	private byte[] BL = new byte[2];
	private int byteCounter = 0;

	public ACK(short opcode) {
		super(opcode);
	}

	public ACK(short opcode, short i1) {
		super(opcode);
	}

	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] Bblock = ByteBuffer.allocate(4).putInt(block).array();
		byte[] ans = new byte[BOpcode.length + Bblock.length];
		
		for (int i=0; i<BOpcode.length; i++){
			ans[i] = BOpcode[i];
		}
		
		for (int i=0; i<Bblock.length; i++){
			ans[BOpcode.length + i] = Bblock[i];
		}

		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		BL[this.byteCounter] = nextByte;
		this.byteCounter++;
		if (byteCounter == 2){
			block = bytesToShort(BL);
			return this;
		}
		else return null;
	}
}
