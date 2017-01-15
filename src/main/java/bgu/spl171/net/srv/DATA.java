package bgu.spl171.net.srv;

import java.nio.ByteBuffer;

public class DATA extends Packet{
	public short packetSize;
	private byte[] PS ;
	public short blockNum;
	private byte[] BL;
	public byte[] data;
	private int byteCounter;

	public DATA(short opCode) {
		super(opCode);
		this.packetSize=0;
		this.blockNum=0;
		data=new byte[512];
		BL = new byte[2];
		PS= new byte[2];
		byteCounter=0;
	}

	public DATA(short opCode,short packetSize, short blockNum, byte[] data) {
		super(opCode);
		this.packetSize=packetSize;
		this.blockNum=blockNum;
		this.data=data;
		//this.data=new byte[512];
		BL = new byte[2];
		PS= new byte[2];
		byteCounter=0;
	}

	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BpacketSize = ByteBuffer.allocate(2).putShort(packetSize).array();
		byte[] Bblock = ByteBuffer.allocate(2).putShort(blockNum).array();
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
		else {
			try {

				data[this.byteCounter - 4] = nextByte;
				byteCounter++;
				if (byteCounter - 4 == packetSize) {
					setFinished();
					return this;
				} else return null;
			}
			catch (ArrayIndexOutOfBoundsException e){
				return null;
			}
		}

	}
}
