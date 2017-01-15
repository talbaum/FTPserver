package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class LOGRQ extends Packet{
	public String username;
	private Vector<Byte> byteVector = new Vector<>();

	public LOGRQ(short opcode) {
		super(opcode);
	}

    public LOGRQ(short opcode, String username) {
        super(opcode);
        this.username=username;
    }

    protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BFL = username.getBytes();
		byte[] ans = new byte[BOpcode.length + BFL.length + 1];
		
		for (int i=0; i<BOpcode.length; i++){
			ans[i] = BOpcode[i];
		}
		
		for (int i=0; i<BFL.length; i++){
			ans[BOpcode.length + i] = BFL[i];
		}
		
		ans[ans.length-1] = '\0';
		
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (nextByte != '\0'){
			byteVector.add(nextByte);
			return null;
		}
		else {
			byte[] byteString = new byte[byteVector.size()];
			for (int i=0; i<byteString.length; i++){
				byteString[i] = byteVector.get(i);
			}
			this.username = new String(byteString, StandardCharsets.UTF_8);
			setFinished();
			return this;
		}
	}
}
