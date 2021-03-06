package bgu.spl171.net.impl.packets;
import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class DELRQ extends Packet{
	public String filename;
	private Vector<Byte> byteVec = new Vector<>();

	public DELRQ(short opcode) {
		super(opcode);
	}

    public DELRQ(short opcode, String filename) {
        super(opcode);
        this.filename=filename;
    }

    public byte[] encode(){
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] filenameBytes=filename.getBytes();
		byte[] ans=new byte[opcodeBytes.length+filenameBytes.length+1];
		
		for (int i=0;i<opcodeBytes.length;i++){
			ans[i]=opcodeBytes[i];
		}
		for (int i=0;i<filenameBytes.length;i++){
			ans[i+opcodeBytes.length]=filenameBytes[i];
		}
		ans[ans.length-1]='0';
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
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
