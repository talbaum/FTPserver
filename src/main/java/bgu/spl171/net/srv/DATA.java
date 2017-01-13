package bgu.spl171.net.srv;

import java.nio.ByteBuffer;

public class DATA extends Packet{
	public short packetSize;
	private byte[] PS = new byte[2];
	public short blockNum;
	private byte[] BL = new byte[2];
	public byte[] data;
	private int byteCounter = 0;
	
	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BpacketSize = ByteBuffer.allocate(4).putInt(packetSize).array();
		byte[] Bblock = ByteBuffer.allocate(4).putInt(blockNum).array();
		byte[] ans = new byte[BOpcode.length + BpacketSize.length + Bblock.length + data.length];
		
		for (int i=0; i<BOpcode.length; i++){
			ans[i] = BOpcode[i];
		}
		
		for (int i=0; i<BpacketSize.length; i++){
			ans[BOpcode.length + i] = BpacketSize[i];
		}
		
		for (int i=0; i<Bblock.length; i++){
			ans[BOpcode.length + BpacketSize.length + i] = Bblock[i];
		}
		
		for (int i=0; i<data.length; i++){
			ans[BOpcode.length + BpacketSize.length + Bblock.length + i] = data[i];
		}
		
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (this.byteCounter < 2){
			PS[this.byteCounter] = nextByte;
			this.byteCounter++;
			return null;
		}
		else if (this.byteCounter == 2){
			packetSize = bytesToShort(PS);
			data = new byte[packetSize];
			BL[0] = nextByte;
			this.byteCounter++;
			return null;
		}
		else if (this.byteCounter == 3){
			BL[1] = nextByte;
			blockNum = bytesToShort(BL);
			this.byteCounter++;
			return null;
		}
		else{
			data[this.byteCounter - 4] = nextByte;
			byteCounter++;
			if (byteCounter - 4 == packetSize) return this;
			else return null;			
		}
	}
}
