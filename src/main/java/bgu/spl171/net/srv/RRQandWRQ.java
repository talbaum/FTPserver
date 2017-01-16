package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;
import java.util.Vector;
/**
 * Created by baum on 10/01/2017.
 */
public class RRQandWRQ extends Packet {
	public String filename;
	private Vector<Byte> byteVec;

	public RRQandWRQ(short opcode) {
		super(opcode);
		this.byteVec = new Vector<>();
		this.filename = "";
	}

	public RRQandWRQ(short opcode, String filename) {
		super(opcode);
		this.byteVec = new Vector<>();
		this.filename = filename;
	}

	protected byte[] encode() {
		byte[] opcodeBytes = shortToBytes(opcode);
		byte[] filenameBytes = filename.getBytes();
		byte[] ans = new byte[opcodeBytes.length + filenameBytes.length + 1];

		for (int i = 0; i < opcodeBytes.length; i++) {
			ans[i] = opcodeBytes[i];
		}
		for (int i = 0; i < filenameBytes.length; i++) {
			ans[i + opcodeBytes.length] = filenameBytes[i];
		}
		ans[ans.length - 1] = '\0';
		return ans;
	}

	@Override
	protected Packet decode(byte nextByte) {
		if (nextByte != '\0') {
			byteVec.add(nextByte);
			return null;
		} else {
			byte[] myStr = new byte[byteVec.size()];
			for (int i = 0; i < myStr.length; i++) {
				myStr[i] = byteVec.get(i);
			}
			this.filename = new String(myStr, StandardCharsets.UTF_8);
			setFinished();
			return this;
		}
	}

	public String getFileName() {
		return filename;
	}
}
