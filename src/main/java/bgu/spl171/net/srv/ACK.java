package bgu.spl171.net.srv;

import java.nio.ByteBuffer;

public class ACK extends Packet{
	public short block;
	private byte[] BL = new byte[2];
	private int byteCounter ;

	public ACK(short opcode) {
		super(opcode);
	}

	public ACK(short opcode, short block) {
		super(opcode);
		this.block=block;
	}

	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] Bblock = ByteBuffer.allocate(2).putShort(block).array();
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
		if (this.byteCounter == 2){
			block = bytesToShort(BL);
			setFinished();
			return this;
		}
		else return null;
	}
}
