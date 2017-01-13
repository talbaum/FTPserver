package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class ERROR extends Packet{
	public short errorCode;
	private byte[] EC = new byte[2];
	public String errMsg;
	private Vector<Byte> byteVector = new Vector<>();
	private int byteCounter = 0;
	
	protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BerrorCode = shortToBytes(errorCode);
		byte[] BerrMsg = errMsg.getBytes();
		byte[] ans = new byte[BOpcode.length + BerrorCode.length + BerrMsg.length + 1];
		
		for (int i=0; i<BOpcode.length; i++){
			ans[i] = BOpcode[i];
		}
		
		for (int i=0; i<BerrorCode.length; i++){
			ans[BOpcode.length + i] = BerrorCode[i];
		}
		
		for (int i=0; i<BerrMsg.length; i++){
			ans[BOpcode.length + BerrorCode.length + i] = BerrMsg[i];
		}
		
		ans[ans.length-1] = '\0';
		
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (this.byteCounter < 2){
			EC[this.byteCounter] = nextByte;
			this.byteCounter++;
			return null;
		}
		else {
			if (this.byteCounter == 3) this.errorCode = bytesToShort(EC);
			if (nextByte != '\0'){
				byteVector.add(nextByte);
				return null;
			}
			else {
				byte[] byteString = new byte[byteVector.size()];
				for (int i=0; i<byteString.length; i++){
					byteString[i] = byteVector.get(i);
				}
				this.errMsg = new String(byteString, StandardCharsets.UTF_8);
				return this;
			}
		}
	}
}
