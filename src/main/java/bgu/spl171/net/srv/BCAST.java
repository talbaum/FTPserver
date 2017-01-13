package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class BCAST extends Packet{
	private byte deletedAdded;
	private String Filename;
	private int byteCounter = 0;
	private Vector<Byte> byteVector = new Vector<>();

	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BFL = Filename.getBytes();
		byte[] ans = new byte[BOpcode.length + BFL.length + 2];
		
		for (int i=0; i<BOpcode.length; i++){
			ans[i] = BOpcode[i];
		}
		
		ans[BOpcode.length] = deletedAdded;
		
		for (int i=0; i<BFL.length; i++){
			ans[BOpcode.length + 1 + i] = BFL[i];
		}
		
		ans[ans.length - 1] = '\0';

		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (this.byteCounter == 0){
			this.deletedAdded = nextByte;
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
	public Vector<Byte> getByteVector() {
		return byteVector;
	}
}
