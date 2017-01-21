package bgu.spl171.net.impl.TFTPtpc.packets;

import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class RRQandWRQ extends Packet {
	public String filename;
	public byte[] data;
	public Vector<Byte> byteVec;
	public int size=0;

	public RRQandWRQ(short opcode) {
		super(opcode);
		this.byteVec = new Vector<>();
		this.filename = "";
		//this.data=new byte[512];
	}
	public RRQandWRQ(short opcode, String filename) {
		super(opcode);
		this.byteVec = new Vector<>();
		this.filename = filename;
		//this.data=new byte[512];
	}

	public RRQandWRQ(short opcode, String filename, DATA packet) {
		super(opcode);
		this.byteVec = new Vector<>();
		this.filename = filename;
		this.data=packet.data;
	}

	public byte[] encode() {
		byte[] opcodeBytes=shortToBytes(opcode);
		byte[] filenameBytes=filename.getBytes();
		byte[] ans =new byte[opcodeBytes.length+filenameBytes.length+1];

		for (int i=0;i<opcodeBytes.length;i++) {
			ans[i]=opcodeBytes[i];
		}
		for (int i=0;i<filenameBytes.length;i++) {
			ans[i+opcodeBytes.length]=filenameBytes[i];
		}
		ans[ans.length-1]='0';
		return ans;
	}

	@Override
	public Packet decode(byte nextByte) {
		if (nextByte!='0') {
			size++;
			byteVec.add(nextByte);
			return null;
		} else {
			byte[] myStr=new byte[byteVec.size()];
			for (int i = 0;i<myStr.length;i++) {
				myStr[i]=byteVec.remove(0);
			}

			this.filename=new String(myStr,0,myStr.length, StandardCharsets.UTF_8);

			this.data=new byte[size-myStr.length];
			for (int i=0;i<data.length;i++)
				data[i]=byteVec.elementAt(i+myStr.length);

			setFinished();
			return this;
		}
	}

	public String getFileName() {
		return filename;
	}
}
