package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class BCAST extends Packet{
	public byte deleteOrAdd;
	public String Filename;
	private int byteCounter = 0;
	private Vector<Byte> byteVector = new Vector<>();


	public BCAST(short opcode) {
		super(opcode);
	}

	public BCAST(short opcode, byte deleteOrAdd, String broadcastMe) {
		super(opcode);
		this.deleteOrAdd=deleteOrAdd;
		this.Filename=broadcastMe;
	}


	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BFL = Filename.getBytes();
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
		if (this.byteCounter == 0){
			this.deleteOrAdd = nextByte;
			this.byteCounter++;
			return null;
		}
		else{			
			if (nextByte != '\0'){
				byteVector.add(nextByte);
				return null;
			}
			else {
				byte[] byteString = new byte[byteVector.size()];
				for (int i=0; i<byteString.length; i++){
					byteString[i] = byteVector.get(i);
				}
				this.Filename = new String(byteString, StandardCharsets.UTF_8);
				return this;
			}
		}
	}

}
