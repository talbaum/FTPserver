package bgu.spl171.net.packets;

import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class ERROR extends Packet{
	public short errorCode;
	public String errMsg;
	private int countMyBytesErr=0;
	private byte[] myError = new byte[2];
	private Vector<Byte> byteVector = new Vector<>();

	public ERROR(short opcode) {
		super(opcode);
		this.countMyBytesErr=0;
	}

    public ERROR(short opcode, short errorCode, String errorMsg) {
        super(opcode);
        this.errorCode=errorCode;
        this.errMsg=errorMsg;
		this.countMyBytesErr=0;
    }

    public byte[] encode(){
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] errCodeBytes=shortToBytes(errorCode);
		byte[] errMsgBytes=errMsg.getBytes();
		byte[] ans=new byte[opcodeBytes.length+errCodeBytes.length+errMsgBytes.length+1];
		for (int i=0;i<opcodeBytes.length;i++){
			ans[i]=opcodeBytes[i];
		}
		for (int i=0; i<errCodeBytes.length; i++){
			ans[i+opcodeBytes.length]=errCodeBytes[i];
		}
		for (int i=0;i<errMsgBytes.length;i++){
			ans[opcodeBytes.length+errCodeBytes.length+i]=errMsgBytes[i];
		}
		ans[ans.length-1]='0';
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
		if (countMyBytesErr ==0 || countMyBytesErr==1){
			myError[countMyBytesErr]=nextByte;
			countMyBytesErr++;
			return null;
		}
		else {
			if (countMyBytesErr==3)
				errorCode=bytesToShort(myError);
			if (nextByte!='\0'){
				byteVector.add(nextByte);
				return null;
			}
			else {
				byte[] byteString=new byte[byteVector.size()];
				for (int i=0;i<byteString.length;i++){
					byteString[i]=byteVector.get(i);
				}
				errMsg=new String(byteString, StandardCharsets.UTF_8);
				setFinished();
				return this;
			}
		}
	}
}
