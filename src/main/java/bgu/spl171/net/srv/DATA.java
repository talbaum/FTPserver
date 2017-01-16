package bgu.spl171.net.srv;
import java.nio.ByteBuffer;
/**
 * Created by baum on 10/01/2017.
 */
public class DATA extends Packet{
	public short packetSize;
	public short blockNum;
	public byte[] data;
	private byte[] sizeBytes ;
	private byte[] blockBytes;
	private int countMyBytesData;

	public DATA(short opCode) {
		super(opCode);
		this.packetSize=0;
		this.blockNum=0;
		data=new byte[512];
		blockBytes = new byte[2];
		sizeBytes= new byte[2];
		countMyBytesData=0;
	}

	public DATA(short opCode,short packetSize, short blockNum, byte[] data) {
		super(opCode);
		this.packetSize=packetSize;
		this.blockNum=blockNum;
		this.data=data;
		blockBytes=new byte[2];
		sizeBytes=new byte[2];
		countMyBytesData=0;
	}

	protected byte[] encode(){
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] blockBytes=ByteBuffer.allocate(2).putShort(blockNum).array();
		byte[] packetBytes=ByteBuffer.allocate(2).putShort(packetSize).array();
		byte[] ans=new byte[opcodeBytes.length + packetBytes.length + blockBytes.length + data.length+1];
		
		for (int i=0;i<opcodeBytes.length;i++){
			ans[i]=opcodeBytes[i];
		}
		for (int i=0;i<packetBytes.length;i++){
			ans[i+opcodeBytes.length]=packetBytes[i];
		}
		for (int i=0;i<blockBytes.length;i++){
			ans[opcodeBytes.length+packetBytes.length+i]=blockBytes[i];
		}
		for (int i=0;i<data.length;i++){
			ans[opcodeBytes.length+packetBytes.length+blockBytes.length+i]=data[i];
		}
		ans[ans.length-1]='\0';
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (countMyBytesData==0||countMyBytesData==1){
			sizeBytes[countMyBytesData]=nextByte;
			countMyBytesData++;
			return null;
		}
		else
			if (countMyBytesData==2){
			packetSize=bytesToShort(sizeBytes);
			data=new byte[packetSize];
			countMyBytesData++;
			blockBytes[0]=nextByte;
			return null;
		}
		else
			if (countMyBytesData==3){
			blockNum=bytesToShort(blockBytes);
			countMyBytesData++;
			blockBytes[1]=nextByte;
			return null;
		}
		else {
			try {
				data[countMyBytesData-4]=nextByte;
				countMyBytesData++;
				if (countMyBytesData-4==packetSize) {
					setFinished();
					return this;
				} else
					return null;
			}
			catch (Exception e){
				return null;
			}
		}
	}
}
