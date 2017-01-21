package bgu.spl171.net.impl.packets;

import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class BCAST extends Packet{
	public String filename;
	public byte deleteOrAdd;
	private int countMyBytesBcast;
	private Vector<Byte> byteVec = new Vector<>();

	public BCAST(short opcode) {
		super(opcode);
		countMyBytesBcast=0;
	}

	public BCAST(short opcode, byte deleteOrAdd, String broadcastMe) {
		super(opcode);
		this.filename=broadcastMe;
		this.deleteOrAdd=deleteOrAdd;
		countMyBytesBcast=0;
	}

	public byte[] encode(){
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] filenameBytes=filename.getBytes();
		byte[] ans=new byte[opcodeBytes.length+filenameBytes.length+2];
		
		for (int i=0;i<opcodeBytes.length;i++){
			ans[i]=opcodeBytes[i];
		}
		ans[opcodeBytes.length]=deleteOrAdd;
		for (int i=0;i<filenameBytes.length;i++){
			ans[opcodeBytes.length+i+1]=filenameBytes[i];
		}
		ans[ans.length-1]='0';
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
		if (countMyBytesBcast==0){
			this.deleteOrAdd=nextByte;
			countMyBytesBcast++;
			return null;
		}
		else{			
			if (nextByte!='0'){
				byteVec.add(nextByte);
				return null;
			}
			else {
				byte[] myStr=new byte[byteVec.size()];
				for (int i=0;i<myStr.length;i++){
					myStr[i]=byteVec.get(i);
				}
				this.filename=new String(myStr, StandardCharsets.UTF_8);
				setFinished();
				return this;
			}
		}
	}
}

