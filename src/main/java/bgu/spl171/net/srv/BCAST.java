package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class BCAST extends Packet{
	public String filename;
	public byte deleteOrAdd;
	private int byteCount = 0;
	private Vector<Byte> byteVec = new Vector<>();


	public BCAST(short opcode) {
		super(opcode);
	}

	public BCAST(short opcode, byte deleteOrAdd, String broadcastMe) {
		super(opcode);
		this.deleteOrAdd=deleteOrAdd;
		this.filename=broadcastMe;
	}


	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(opcode);
		byte[] BFL = filename.getBytes();
		byte[] ans = new byte[BOpcode.length + BFL.length + 2];
		
		for (int i=0; i<BOpcode.length; i++){
			ans[i] = BOpcode[i];
		}
		
		ans[BOpcode.length] = deleteOrAdd;
		
		for (int i=0; i<BFL.length; i++){
			ans[BOpcode.length + 1 + i] = BFL[i];
		}
		
		ans[ans.length - 1] = '\0';

		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (this.byteCount== 0){
			this.deleteOrAdd = nextByte;
			this.byteCount++;
			return null;
		}
		else{			
			if (nextByte != '\0'){
				byteVec.add(nextByte);
				return null;
			}
			else {
				byte[] byteString = new byte[byteVec.size()];
				for (int i=0; i<byteString.length; i++){
					byteString[i] = byteVec.get(i);
				}
				this.filename = new String(byteString, StandardCharsets.UTF_8);
				setFinished();
				return this;
			}
		}
	}

}
