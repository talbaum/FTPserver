package bgu.spl171.net.srv;

import java.nio.charset.StandardCharsets;

public class EncdecTest {
	public static void main (String args[]){
	
	ImplMsgEncDec encdec = new ImplMsgEncDec();

 /* * * * * * * * * * * * * * * * * * * * * * * * * * * 
                TESTING THE ENCODER DECODER
                /* * * * * * * * * * * * * * * * * * * * * * * * * * * */

 // Instructions: 1. Change the names I used for your names.
 //               2. Import the thing you need
 //               3. Remove the "//" from the packet you want to test, and run it.
 //                  You can activate decode and then encode in order to see that you receive the same output as you started.
 //               *. Some of the tests are not relevant - You need to encode just: data, ack, bcast, and error. 
//  testRRQDecode(encdec); // 1
   testWRQDecode(encdec); // 2
     testDataDecode(encdec); // 3
	testDataEncode(encdec); // 3
//	testACKDecode(encdec); // 4
//	testACKEncode(encdec); // 4
//	testErrorDecode(encdec); // 5
//	testErrorEncode(encdec); // 5
//	testDIRQDecode(encdec); // 6
 //	testLOGRQDecode(encdec); // 7
 //	testDELRQDecode(encdec); // 8
 //	testBCastDecode(encdec); // 9
//	testBCastEncode(encdec); // 9
	//testDISCDecode(encdec); // 10
   
	}
//
//	public static void testBIGDataDecode (MessageEncoderDecoderImpl encdec){
//		byte[] bigData = new byte[567];
//		for (int i=0; i<bigData.length;i++)
//			bigData[i]=(byte)i;
//		System.out.println("FLAG Data");
//		byte[] b = {0,3,2,55,1,5}; //
//		byte[] append = new byte[b.length+bigData.length];
//		for (int i=0; i<b.length; i++)
//			append[i]=b[i];
//		for (int i=b.length; i<append.length; i++)
//			append[i]=bigData[i-b.length];
//		System.out.println("FLAG Append");
//		// bytesToShort({2,55})=(short)567 - The packetSize, bytesToShort({1,5})=(short)261 - The blockNum
//		ServerPacket res=null;
//		System.out.println("Before decoding, the Arr is");
//		printArr(append);
//		for (int i=0; i<append.length; i++)
//			res=encdec.decodeNextByte(append[i]);
//		short opcode=((DataPacket)res).getOpcode();
//		short packetSize=((DataPacket)res).getPacketSize();
//		short blockNum=((DataPacket)res).getBlockNum();
//		byte[] dataBytes=((DataPacket)res).getData();
//		System.out.println("After decoding the arr, we've got a packet!");
//		System.out.println("The opcode is " + opcode + " The packetSize is " + packetSize +"  and the blockNum is " + blockNum);
//		System.out.println("The data is ");
//		printArr(dataBytes);
//	}
//
//	public static void testBIGDataEncode (MessageEncoderDecoderImpl encdec){
//		byte[] bigData = new byte[567];
//		for (int i=0; i<bigData.length;i++)
//			bigData[i]=(byte)i;
//		System.out.println("FLAG Data");
//		DataPacket packet = new DataPacket(((short)3), ((short)255), ((short)261), bigData);
//		byte[] res = encdec.encode(packet);
//		System.out.println("Encoding the packet " + packet.getOpcode() + " is the Opcode "+ packet.getPacketSize() + " is the packetSize " + packet.getBlockNum() + " is the Block Num " );
//		System.out.println("The data arr is " );
//		printArr(bigData);
//		System.out.print("Output: ");
//
//		printArr(res);
//	//	System.out.println("The output should be... long");
//	}


	public static void testDataDecode (ImplMsgEncDec encdec){
		byte[] b = {0,3,0,5,1,5,1,2,3,4,5}; // 0,5 is the packetSize(5), 1,5 is the blockNum(261)
		// bytesToShort({0,5})=(short)5, bytesToShort({1,5})=(short)261
		Packet res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=res.getOpcode();
		short packetSize=((DATA)res).packetSize;
		short blockNum=((DATA)res).blockNum;
		byte[] dataBytes=((DATA)res).data;
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode + " The packetSize is " + packetSize +"  and the blockNum is " + blockNum);
		System.out.println("The data is ");
		printArr(dataBytes);
	}
	
	public static void testDataEncode (ImplMsgEncDec encdec){
		byte[] b = {1,2,3,4,5};
		DATA packet = new DATA(((short)3), ((short)5), ((short)261), b);
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " is the Opcode "+ packet.packetSize + " is the packetSize " + packet.blockNum + " is the Block Num " );
		System.out.println("The data arr is " );
		printArr(b);
		System.out.print("Output: ");
	
		printArr(res); // Should be {0,3,0,5,1,5,1,2,3,4,5}
		System.out.println("The output should be {0,3,0,5,1,5,1,2,3,4,5}");
	}
	
/*
	public static void testDISCDecode (ImplMsgEncDec encdec){
		byte[] b = {0,10}; 
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((DiscPacket)res).getOpcode();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode);
	}
	
	public static void testDISCEncode (ImplMsgEncDec encdec){
		DiscPacket packet = new DiscPacket((short)10);
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " is the Opcode");
		System.out.print("Output: ");

		printArr(res); // Should be {0,10}
		System.out.println("The output should be {0,10}");
	}

	public static void testBCastDecode (ImplMsgEncDec encdec){
		byte[] b = {0,9,1,66,67,97,115,116,83,116,114,0}; 
		// popString({66,67,97,115,116,83,116,114})=(String)"BCastStr"
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((BCastPacket)res).getOpcode();
		short deleted_or_added=((BCastPacket)res).getDelOrAdd();
		String Filename=((BCastPacket)res).getFileName();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode + " the deleted_or_added is " + deleted_or_added +"  and the Filename is " + Filename);
	}
	
	public static void testBCastEncode (ImplMsgEncDec encdec){
		BCastPacket packet = new BCastPacket(((short)9), (byte) 1, "BCastStr");
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " is the Opcode " + packet.getDelOrAdd() + " is the deleted_or_added code " + packet.getFileName());
		System.out.print("Output: ");

		printArr(res); // Should be {0,9,1,66,67,97,115,116,83,116,114,0}
		System.out.println("The output should be {0,9,1,66,67,97,115,116,83,116,114,0}");
	}

	public static void testDELRQDecode (ImplMsgEncDec encdec){
		byte[] b = {0,8,68,97,110,97,0};
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((DELRQPacket)res).getOpcode();
		String fileName=((DELRQPacket)res).getFileName();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode +" and the fileName is " + fileName);
	}
	
	public static void testDELRQEncode (ImplMsgEncDec encdec){
		DELRQPacket packet = new DELRQPacket((short) 8, "Dana");
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " Opcode " + packet.getFileName());
		System.out.print("Output: ");

		printArr(res); // Should be {0,8,68,97,110,97,0}
		System.out.println("The output should be {0,8,68,97,110,97,0}");
	}
	
	
	public static void testLOGRQDecode (ImplMsgEncDec encdec){
		byte[] b = {0,7,68,97,110,97,0};
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((LOGRQPacket)res).getOpcode();
		String userName=((LOGRQPacket)res).getUserName();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode +" and the userName is " + userName);
	}
	
	public static void testLOGRQEncode (ImplMsgEncDec encdec){
		LOGRQPacket packet = new LOGRQPacket((short) 7, "Dana");
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " Opcode " + packet.getUserName());
		System.out.print("Output: ");

		printArr(res); // Should be {0,7,68,97,110,97,0}
		System.out.println("The output should be {0,7,68,97,110,97,0}");
	}
	
	
	public static void testDIRQDecode (ImplMsgEncDec encdec){
		byte[] b = {0,6}; 
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((DIRQPacket)res).getOpcode();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode);
	}
	
	public static void testDIRQEncode (ImplMsgEncDec encdec){
		DIRQPacket packet = new DIRQPacket((short)6);
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " is the Opcode");
		System.out.print("Output: ");

		printArr(res); // Should be {0,6}
		System.out.println("The output should be {0,6}");
	}
	
 	
	public static void testErrorDecode (ImplMsgEncDec encdec){
		byte[] b = {0,5,14,20 ,69,114,114,111,114,32,75,97,112,97,114,97 ,0}; 
		// bytesToShort({14,20})=(short)3604, and popString({69,114,114,111,114,32,75,97,112,97,114,97})=(String)"Error Kapara"
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((ErrorPacket)res).getOpcode();
		short errorCode=((ErrorPacket)res).getErrorCode();
		String errorMsg=((ErrorPacket)res).getErrMsg();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode + " The Error code is " + errorCode +"  and the error messege is " + errorMsg);
	}
	
	public static void testErrorEncode (ImplMsgEncDec encdec){
		ErrorPacket packet = new ErrorPacket((short)5, (short)3604, "Error Kapara");
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " is the Opcode " + packet.getErrorCode() + " is the error code " + packet.getErrMsg());
		System.out.print("Output: ");
	
		printArr(res); // Should be {0,5,14,20 ,69,114 ,114,111,114,32,75,97,112,97,114,97 ,0}
		System.out.println("The output should be {0,5,14,20,69,114,114,111,114,32,75,97,112,97,114,97,0}");
	}
	
	public static void testRRQDecode (ImplMsgEncDec encdec){
		byte[] b = {0,1,68,97,110,97,0};
		ServerPacket res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((RRQPacket)res).getOpcode();
		String fileName=((RRQPacket)res).getFileName();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode +" and the fileName is " + fileName);
	}
	
	public static void testRRQEncode (ImplMsgEncDec encdec){
		RRQPacket packet = new RRQPacket((short) 1, "Dana");
		byte[] res = encdec.encode(packet);
		System.out.println("Encoding the packet " + packet.getOpcode() + " Opcode " + packet.getFileName());
		System.out.print("Output: ");

		printArr(res); // Should be {0,1,68,97,110,97,0}
		System.out.println("The output should be {0,1,68,97,110,97,0}");
	}
	*/
	public static void testWRQDecode (ImplMsgEncDec encdec){
		byte[] b = {0,2,68,97,110,97,0};
		Packet res=null;
		System.out.println("Before decoding, the Arr is");
		printArr(b);
		for (int i=0; i<b.length; i++)
			res=encdec.decodeNextByte(b[i]);
		short opcode=((RRQandWRQ)res).getOpcode();
		String fileName=((RRQandWRQ)res).getFileName();
		System.out.println("After decoding the arr, we've got a packet!");
		System.out.println("The opcode is " + opcode +" and the fileName is " + fileName);
	}
	/*
        public static void testWRQEncode (ImplMsgEncDec encdec){
            RRQandWRQ packet = new RRQandWRQ((short) 2, "Dana");
            byte[] res = encdec.encode(packet);
            System.out.println("Encoding the packet " + packet.getOpcode() + " Opcode " + packet.getFileName());
            System.out.print("Output: ");

            printArr(res); // Should be {0,2,68,97,110,97,0}
            System.out.println("The output should be {0,2,68,97,110,97,0}");
        }

        public static void testACKDecode (ImplMsgEncDec encdec){
            byte[] b = {0,4,14,20}; // bytesToShort({14,20})=(short)3604
            ServerPacket res=null;
            System.out.println("Before decoding, the Arr is");
            printArr(b);
            for (int i=0; i<b.length; i++)
                res=encdec.decodeNextByte(b[i]);
            short opcode=((AckPacket)res).getOpcode();
            short blockNum=((AckPacket)res).getBlockNum();
            System.out.println("After decoding the arr, we've got a packet!");
            System.out.println("The opcode is " + opcode +" and the blockNum is " + blockNum);
        }

        public static void testACKEncode (MessageEncoderDecoderImpl encdec){
            AckPacket packet = new AckPacket((short) 4, ((short)3604)); // bytesToShort({14,20})=(short)3604
            byte[] res = encdec.encode(packet);
            System.out.println("Encoding the packet " + packet.getOpcode() + " Opcode " + packet.getBlockNum());
            System.out.print("Output: ");

            printArr(res); // Should be {0,2,68,97,110,97,0}
            System.out.println("The output should be {0,4,14,20}");
        }

        */
	public static void printArr(byte[] b){
	//	System.out.print("Output: ");
		for (int i=0; i<b.length; i++)
			System.out.print(b[i]+"-");
		System.out.println();
	}


    public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    
    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
    
    public static byte[] popTwoFirstBytes(byte[] dataToDecode){
		byte[] newDataToDecode= new byte[dataToDecode.length-2];
		for(int i=0; i<newDataToDecode.length;i++)
			newDataToDecode[i]=dataToDecode[i+2];
		return newDataToDecode;
    }
    
    
    public static String popString(byte[] bytes) {
        //notice that we explicitly requesting that the string will be decoded from UTF-8
        //this is not actually required as it is the default encoding in java.
    	int len=bytes.length;
        String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return result;
    }
    
}