package bgu.spl171.net.packets;

import bgu.spl171.net.api.Message;

import java.io.FileOutputStream;
import java.util.LinkedList;

/**
 * Created by בbaum on 08/01/2017.
 */
public class DATA implements Message{
       public String Data(byte[] array, int size, int blockNum){
           //we need to keep the data at some way

           byte[] byteArray=null;
           for(int i=0;i<array.length;i++)
           singleFileData.add(array[i]);

           if (size<512) {
               byteArray=new byte[singleFileData.size()];
               int i=0;
               while (!singleFileData.isEmpty()) {
                  byteArray[i]= singleFileData.pollFirst();
                  i++;
               }
               if(!byteToFile(byteArray))
                   return ERROR.getError(2,"");
           }

           return ACK.checkACK(blockNum, true);
       }

       private boolean byteToFile(byte[] saveMe){
        //here we will write all the bytes to the Files
           try {
               FileOutputStream fos = new FileOutputStream("\"C:\\\\Users\\\\באום\\\\Desktop\\\\SPL\\\\Intelij Projects\\\\SPL3\\\\net\\\\src\\\\main\\\\java\\\\bgu\\\\spl171\\\\net\\\\srv\\\\Files\"");
               fos.write(saveMe);
               fos.close();
           return true;
           }
           catch (Exception e){
               return false;
           }
       }
    @Override
    public short getOpCode() {
        return 3;
    }
}
