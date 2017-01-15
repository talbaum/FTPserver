package bgu.spl171.net.srv;

abstract public class Packet{
	short Opcode;
	boolean isFinished;

	public Packet(short opcode){
		this.Opcode=opcode;
		isFinished=false;
	}
	public byte[] shortToBytes(short num)
	{
	    byte[] bytesArr = new byte[2];
	    bytesArr[0] = (byte)((num >> 8) & 0xFF);
	    bytesArr[1] = (byte)(num & 0xFF);
	    return bytesArr;
	}

	protected abstract Packet decode(byte nextByte);
	
	public short bytesToShort(byte[] byteArr)
	{
	    short result = (short)((byteArr[0] & 0xff) << 8);
	    result += (short)(byteArr[1] & 0xff);
	    return result;
	}
	public short getOpcode(){
		return this.Opcode;
	}

	public boolean isFinished(){
		return  this.isFinished;
	}
	public void setFinished(){
		isFinished=true;
	}
}
