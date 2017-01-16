package bgu.spl171.net.srv;
import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.Connections;


import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

/**
 * Created by baum on 10/01/2017.
 */
public class TFTPprotocol<T> implements BidiMessagingProtocol<T> {

    int ID;
    ConnectionsImpl connections;
    ConcurrentHashMap<String, LinkedList<Byte>> files = new ConcurrentHashMap<>();
    LinkedList<Byte> singleFileData = new LinkedList<>();
    final LinkedList<Byte> EMPTYLIST = new LinkedList<>();
    boolean isBcast = false;
    boolean isLogged;
    boolean firstWriteFlag=true;
    boolean firstReadFlag=true;
    boolean moreDataNeeded;
    boolean dataGotAck;
    String fileToWrite;
    int readBlockingCount=0;
    int writeBlockingCount=0;
    boolean writeHasFinished=false;

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = (ConnectionsImpl) connections;
        this.ID = connectionId;
        this.isLogged = false;
    }

    @Override
    public void process(T message) {
        Packet tmp = (Packet) message;
        short OP = tmp.getOpcode();
        byte[] ans = null;

        if (!isLogged && OP != 7) {
            ans = getError(6, ""); //user not logged in- cant make actions
        } else {
            switch (OP) {
                case 1:
                    ans = RRQhandle(tmp);
                    break;
                case 2:
                    ans = WRQhandle(tmp);
                    break;
                case 3: ans=DataHandle(tmp);
                    break;
                case 4:ans=AckHandle(tmp);
                    break;
                case 5:ans = ErrorHandle(tmp);
                    break;
                case 6:ans=DirqHandle(tmp);
                    break;
                case 7:ans=LogrqHandle(tmp);
                    break;
                case 8:ans=DelrqHandle(tmp);
                    break;
                case 9:ans=BcastHandle(tmp);
                    break;
                case 10:ans=DiscHandle(tmp);
                    break;
            }
        }
        if (!isBcast)
            connections.send(ID, ans);
        else
            isBcast = false;
    }


    @Override
    public boolean shouldTerminate() {
        return !isLogged;
    } //need to make sure

    private byte[] checkACK(int blockNum, boolean isData) {
        if (!isData){
            ACK ans= new ACK(((short)05), (short) 0);
            return ans.encode();
        }
        else {
            ACK ans= new ACK(((short)05), (short) blockNum);
            return ans.encode();
        }
    }


    private byte[] getError(int errorCode, String errorMsg) {
    short myOp= (short)errorCode;
    String errorMsg2="No error messege was acquired";
        switch (errorCode) {
            case (short)0:
                errorMsg2= ("Not defined, see error message (if any). " + errorMsg); break;
            case (short)1:
                errorMsg2 =("File not found – RRQ of non-existing file  " + errorMsg); break;
            case (short)2:
                errorMsg2= ("Access violation – File cannot be written, read or deleted.   " + errorMsg); break;
            case (short)3:
                errorMsg2= ("Disk full or allocation exceeded – No room in disk " + errorMsg);break;
            case (short)4:
                errorMsg2= ("Illegal TFTP operation – Unknown Opcode  " + errorMsg);break;
            case (short)5:
                errorMsg2= ("File already exists – File name exists on WRQ.  " + errorMsg);break;
            case (short)6:
                errorMsg2= ("User not logged in – Any opcode received before Login completes.  " + errorMsg);break;
            case (short)7:
                errorMsg2= ("User already logged in – Login username already connected.  " + errorMsg);break;
        }
        ERROR er=new ERROR(myOp,(short)errorCode,errorMsg2);
        return er.encode();
    }

    private boolean byteToFile(byte[] tmp, String name) {
        Path p = Paths.get("./Files"+name);
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, CREATE, APPEND))) {
            out.write(tmp, 0, tmp.length);
            return true;
        } catch (IOException x) {
            System.err.println(x);
            return false;
        }
    }


    private byte[] read(String readMe) {
        byte[] arr = new byte[512];
        LinkedList<Byte> readedFileBytes = files.get(readMe);
        int i = 0;
        while (i < 512 && !readedFileBytes.isEmpty()) {
            arr[i] = readedFileBytes.pollFirst();
            i++;
        }
     /*   if (readedFileBytes.isEmpty())
            removeFromFilesFolder(readMe);*/

        return arr;
    }
    private byte[] readHelper(String fileToRead){
        if (files.get(fileToRead).isEmpty()) {
            String letAllKnowRead = fileToRead + " has completed uploading to the server.";
            connections.broadcast(letAllKnowRead.getBytes()); // returns only to the client
            isBcast = true;
            readBlockingCount = 0;
            moreDataNeeded=false;
            return null;
        }
        else {
            byte[] curData = read(fileToRead); //change to good return op code bock
            short sizeOfData = (short) curData.length;
            DATA ans = new DATA((short) 03, sizeOfData, (short) readBlockingCount, curData);
            readBlockingCount++;
            moreDataNeeded=true;
            return ans.encode();
        }
    }

    private byte[] RRQhandle(Packet tmp) {
      byte[]ans=null;
        String fileToRead = ((RRQandWRQ) tmp).getFileName();
        if (firstReadFlag) {
            if (files.containsKey(fileToRead))
                ans= readHelper(fileToRead);
            else
                ans= getError(1, ""); //file not found for reading

            if(ans!=null)
                firstReadFlag=false;
            else
                firstReadFlag=true;
        }
        else{
            if(dataGotAck){
            dataGotAck=false;
            ans= readHelper(fileToRead);
            }
        }
        return ans;
    }
    private byte[] WRQhandle(Packet tmp) {
        fileToWrite = ((RRQandWRQ) tmp).getFileName();
        if (!files.containsKey(fileToWrite)) {

            if (firstWriteFlag) {
                files.put(fileToWrite, EMPTYLIST);
                firstWriteFlag = false;
                return checkACK(0, false);
            }
            else {
                byte[] myCurData = ((RRQandWRQ) tmp).data;
                short myCurSize = (short) myCurData.length;
                DATA fileData = new DATA((short) 03, myCurSize, (short) writeBlockingCount, myCurData);
                byte[] ans = DataHandle(fileData);

                if (writeHasFinished) {
                    connections.send(ID, ans); //send the last ACK to the client
                    String letAllKnow = fileToWrite + " has completed uploading to the server.";
                    connections.broadcast(letAllKnow.getBytes());// check if to my client
                    isBcast = true; //cause of this it wont send it to the client again
                    writeHasFinished=false;
                    firstWriteFlag=true;
                }
                return ans;
            }
        } else
            return getError(5, ""); //file already exist
    }
    private byte[] DataHandle(Packet tmp) {
        byte[] byteArray = ((DATA) tmp).data;

        for (int i = 0; i < byteArray.length; i++)
            singleFileData.add(byteArray[i]);

        int tmpCount=writeBlockingCount;
        writeBlockingCount++;

        if (((DATA) tmp).packetSize < 512) {
            files.replace(fileToWrite, EMPTYLIST, singleFileData);
            byteArray = new byte[singleFileData.size()];

            int i = 0;
            while (!singleFileData.isEmpty()) {
                byteArray[i] = singleFileData.pollFirst();
                i++;
            }

            if (!byteToFile(byteArray,fileToWrite)) {
                return getError(2, ""); //cannot write error
            }
            else {
               /* letAllKnow = fileToWrite + " has completed uploading to the server.";
                connections.broadcast(letAllKnow.getBytes());// check if to my client
                isBcast=true;*/
                //return null;

                writeHasFinished=true;

              //  return checkACK(tmpCount,true);
            }
        }
            return checkACK(tmpCount, true);
    }

    private byte[] AckHandle(Packet tmp) {
        if (moreDataNeeded) {
           dataGotAck=true; //this is a data block, can send another block of data
            return checkACK(((DATA) tmp).blockNum, true);
        } else {
            return checkACK(0, false);
        }
    }
    private byte[] ErrorHandle(Packet tmp) {
        return getError(((ERROR) tmp).errorCode, ((ERROR) tmp).errMsg);
    }

    private byte[] DirqHandle(Packet tmp) { //switch to good return
        String allFilesNames = "";
    for (String nameOfFile : files.keySet()) {
        allFilesNames += nameOfFile + " \0 ";
    }

    if (allFilesNames.equals(""))
        return getError(0, "No Files to show");
    else{
        byte[]fileNamesBytes= allFilesNames.getBytes();
        short sizeOfByteArr=(short)fileNamesBytes.length;

        DATA ans= new DATA((short)06, sizeOfByteArr,(short)1,fileNamesBytes);
            return ans.encode();
    }
}

private byte[] LogrqHandle(Packet tmp) {
    String username = ((LOGRQ) tmp).username;
    if (!connections.MyConnections.contains(username)) {
        connections.MyConnections.put(ID, username);
        isLogged = true;
        return checkACK(0, false);
    } else
        return getError(7, ""); //user already logged in
}

    private boolean removeFromFilesFolder(String deleteMe) {
        try {
            //check for generic path
            Path p1 = Paths.get("C:\\Users\\באום\\Desktop\\SPL\\Intelij_Projects\\SPL3\\net\\Files\\" + deleteMe);
            Files.delete(p1);
            files.remove(deleteMe);
            return true;
        } catch (NoSuchFileException x) {
            System.out.println("no such file or directory");
            return false;
        } catch (DirectoryNotEmptyException x) {
            System.out.println("file is not empty");
            return false;
        } catch (IOException x) {
            System.err.println(x);
            return false;
        }
    }
    private byte[] DelrqHandle(Packet tmp){
        String filenameToDel = ((DELRQ) tmp).filename;
        if (files.containsKey(filenameToDel)) {
            files.remove(filenameToDel);
            if (removeFromFilesFolder(filenameToDel))
                return checkACK(0, false);
            else
                return getError(2, ""); //cannot read violation
        } else
            return getError(1, ""); //file not found
    }
    private byte[] BcastHandle(Packet tmp) {
        connections.broadcast(((BCAST) tmp).encode());
        isBcast = true;
        return null;
    }
    private byte[] DiscHandle(Packet tmp) {
        connections.disconnect(ID);
        isLogged = false;
        return checkACK(0, false);
    }

    public short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }
    public byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}