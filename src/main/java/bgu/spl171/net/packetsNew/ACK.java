package bgu.spl171.net.packetsNew;

import java.nio.ByteBuffer;

public class ACK extends Packet{
	private int block;
	private byte[] BL = new byte[2];
	private int byteCounter = 0;
	
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
