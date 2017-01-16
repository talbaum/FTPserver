package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class BCAST extends Packet{

	public String filename;
	public byte deleteOrAdd;
	private int countMyBytes;
	private Vector<Byte> byteVec = new Vector<>();


	public BCAST(short opcode) {
		super(opcode);
		countMyBytes=0;
	}

	public BCAST(short opcode, byte deleteOrAdd, String broadcastMe) {
		super(opcode);
		this.filename=broadcastMe;
		this.deleteOrAdd=deleteOrAdd;
		countMyBytes=0;
	}

	protected byte[] encode(){

		byte[] opcodeBytes = shortToBytes(opcode);
		byte[] filenameBytes = filename.getBytes();
		byte[] ans = new byte[opcodeBytes.length + filenameBytes.length + 2];
		
		for (int i=0; i<opcodeBytes.length; i++){
			ans[i] = opcodeBytes[i];
		}
		ans[opcodeBytes.length] = deleteOrAdd;

		for (int i=0; i<filenameBytes.length; i++){
			ans[opcodeBytes.length+i+1] = filenameBytes[i];
		}
		ans[ans.length-1]='\0';
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {

		if (this.countMyBytes== 0){
			this.deleteOrAdd = nextByte;
			this.countMyBytes++;
			return null;
		}
		else{			
			if (nextByte !='\0'){
				byteVec.add(nextByte);
				return null;
			}
			else {
				byte[] myStr = new byte[byteVec.size()];
				for (int i=0; i<myStr.length; i++){
					myStr[i] = byteVec.get(i);
				}
				this.filename = new String(myStr, StandardCharsets.UTF_8);
				setFinished();
				return this;
			}
		}
	}

}
