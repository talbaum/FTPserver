package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;

public class RRQandWRQ extends Packet{
	private String Filename;
	private Vector<Byte> byteVector = new Vector<>();

	public RRQandWRQ(short opcode) {
		super(opcode);
	}

    public RRQandWRQ(short opcode, String filename) {
        super(opcode);
        this.Filename=filename;
    }

    protected byte[] encode(){
		
		byte[] BOpcode = shortToBytes(Opcode);
		byte[] BFL = Filename.getBytes();
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
			this.Filename = new String(byteString, StandardCharsets.UTF_8);
			return this;
		}
	}
	public String getFileName() {
		return Filename;
	}
	
}
