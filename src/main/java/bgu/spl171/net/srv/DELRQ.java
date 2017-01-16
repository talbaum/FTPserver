package bgu.spl171.net.srv;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class DELRQ extends Packet{
	public String filename;
	private Vector<Byte> byteVector = new Vector<>();

	public DELRQ(short opcode) {
		super(opcode);
	}

    public DELRQ(short opcode, String filename) {
        super(opcode);
        this.filename=filename;
    }

    protected byte[] encode(){
		byte[] opcodeBytes = shortToBytes(opcode);
		byte[] filenameBytes = filename.getBytes();
		byte[] ans = new byte[opcodeBytes.length+filenameBytes.length+1];
		
		for (int i=0; i<opcodeBytes.length; i++){
			ans[i] = opcodeBytes[i];
		}
		for (int i=0; i<filenameBytes.length; i++){
			ans[i+opcodeBytes.length]=filenameBytes[i];
		}
		ans[ans.length-1] = '\0';
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (nextByte!='\0'){
			byteVector.add(nextByte);
			return null;
		}
		else {
			byte[] byteString = new byte[byteVector.size()];
			for (int i=0; i<byteString.length; i++){
				byteString[i] = byteVector.get(i);
			}
			this.filename = new String(byteString, StandardCharsets.UTF_8);
			setFinished();
			return this;
		}
	}

}
