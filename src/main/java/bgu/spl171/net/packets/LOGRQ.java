package bgu.spl171.net.packets;

import java.util.Vector;
import java.nio.charset.StandardCharsets;

/**
 * Created by baum on 10/01/2017.
 */
public class LOGRQ extends Packet{
	public String username;
	private Vector<Byte> byteVec = new Vector<>();

	public LOGRQ(short opcode) {
		super(opcode);
		encode();
	}

    public LOGRQ(short opcode, String username) {
        super(opcode);
        this.username=username;
    }

    public byte[] encode(){
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] usernameBytes=username.getBytes();
		byte[] ans = new byte[opcodeBytes.length+usernameBytes.length+1];
		for (int i=0;i<opcodeBytes.length;i++){
			ans[i]=opcodeBytes[i];
		}
		for (int i=0;i<usernameBytes.length;i++){
			ans[i+opcodeBytes.length]=usernameBytes[i];
		}
		ans[ans.length-1]='\0';
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
		if (nextByte!='\0'){
			byteVec.add(nextByte);
			return null;
		}
		else {
			byte[] myStr=new byte[byteVec.size()];
			for (int i=0;i<myStr.length;i++){
				myStr[i]=byteVec.get(i);
			}
			this.username=new String(myStr, StandardCharsets.UTF_8);
			setFinished();
			return this;
		}
	}
}
