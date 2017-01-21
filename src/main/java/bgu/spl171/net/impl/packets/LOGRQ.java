package bgu.spl171.net.impl.packets;

import java.util.Vector;

/**
 * Created by baum on 10/01/2017.
 */
public class LOGRQ extends Packet{
	public String username;
	public Vector<Byte> byteVec = new Vector<>();

	public LOGRQ(short opcode) {
		super(opcode);
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
		ans[ans.length-1]=0;
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
		if (nextByte!='0'){
			byteVec.add(nextByte);
			return null;
		}
		else {
			byte[] myStrbytes=new byte[byteVec.size()];
			for (int i=0;i<myStrbytes.length;i++){
				myStrbytes[i]=byteVec.get(i);
			}
			try{
				this.username=new String(myStrbytes, "UTF-8");
			}
			catch (Exception e){
				System.out.println("username problem");
			}

			setFinished();
			return this;
		}
	}
}
