package bgu.spl171.net.impl;
import bgu.spl171.net.impl.api.BidiMessagingProtocol;
import bgu.spl171.net.impl.api.Connections;
import bgu.spl171.net.impl.packets.*;


import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by baum on 10/01/2017.
 */
public class TFTPprotocol<T> implements BidiMessagingProtocol<T> {

    int ID;
    public ConnectionsImpl connections;
    LinkedList<String> loggedUsers= new LinkedList<>();
    String loggedUsername;
    LinkedList<Byte> singleFileData = new LinkedList<>();
    boolean isBcast = false;
    boolean isLogged;
    boolean firstWriteFlag=true;
    boolean waitingForWrite=false;
    boolean readedData=false;
    boolean moreDataNeeded;
    boolean dataGotAck;
    String fileToWrite;
    int writeBlockingCount=1;
    boolean writeHasFinished=false;

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = (ConnectionsImpl) connections;
        this.ID = connectionId;
        this.isLogged = false;
    }

    @Override
    public  void process(T message) {

        Packet tmp = (Packet) message;
        short OP = tmp.getOpcode();
        Packet ans = null;

        if (!isLogged && OP != 7) {
            ans = getError(6, ""); //user not logged in- cant make actions
        } else {
            switch (OP) {
                case 1:
                    ans = RRQhandle(tmp);
                    break;
                case 2:
                    ans=WRQhandle(tmp);
                    waitingForWrite=true;
                        break;
                case 3:
                    ans = DataHandle(tmp);
                    break;
                case 4:
                    ans = AckHandle(tmp);
                    break;
                case 5:
                    ans = ErrorHandle(tmp);
                    break;
                case 6:
                    ans = DirqHandle(tmp);
                    break;
                case 7:
                    ans = LogrqHandle(tmp);
                    break;
                case 8:
                    ans = DelrqHandle(tmp);
                    break;
                case 9:
                    ans = BcastHandle(tmp);
                    break;
                case 10:
                    ans = DiscHandle(tmp);
                    break;
            }
        }
        if (!isBcast && !readedData) {
            connections.send(ID, ans);

            if(shouldTerminate())
                connections.disconnect(ID);
        }else {
            if(isBcast) {
                connections.broadcast(ans);
                isBcast = false;
            }
            else{
                readedData=false;
            }
            }
    }


    @Override
    public boolean shouldTerminate() {
        return !isLogged;
    }

    private ACK checkACK(int blockNum, boolean isData) {
        if (!isData){
            ACK ans= new ACK(((short)04), (short) 0);
            ans.setFinished();
            return ans;
        }
        else {
            ACK ans= new ACK(((short)04), (short) blockNum);
          ans.setFinished();
            return ans;
        }
    }

    private ERROR getError(int errorCode, String errorMsg) {
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
        ERROR er=new ERROR((short)5,(short)errorCode,errorMsg2);
          er.setFinished();
        return er;
    }

    private boolean byteToFile(byte[] tmp, String name) {
        FileOutputStream fos = null;
        File file;
        try {
            file = new File("Files" + File.separator + name);
            fos = new FileOutputStream(file);
            if (!file.exists()) {
                file.createNewFile();
            }
            fos.write(tmp);
            fos.flush();
            return true;
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            try {
                if (fos != null)
                    fos.close();
            }
            catch (IOException ioe) {
                return false;
            }
        }
    return false;
    }

    private DATA countAndDivideBytes(File file) {
        int sizeOfFiles = 512;
        byte[] buffer = new byte[sizeOfFiles];// buffer for max size of Data
        BufferedInputStream bis=null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        int tmpSize = 0;
        int partCounter = 1;

        try {
            while ((tmpSize = bis.read(buffer)) > 0) {
                byte[] dataBuffer= Arrays.copyOf(buffer, tmpSize+1);
                dataBuffer[tmpSize]='0';
                byte[]ansArr= new byte[tmpSize+1];
                for(int i=0;i<dataBuffer.length;i++)
                    ansArr[i]=dataBuffer[i];

                DATA dataPacket= new DATA((short) 3 ,(short)partCounter,(short)tmpSize, ansArr );
                partCounter++;
                dataPacket.setFinished();
                readedData=true;
                connections.send(ID,dataPacket);

                return dataPacket;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Packet RRQhandle(Packet tmp) {
        Packet ans=null;
        String fileToRead = ((RRQandWRQ) tmp).getFileName();
            if (searchTheFileInFolder(fileToRead)) {
                File files = new File("Files");
                String[] dirlist = files.list();
                for (int i = 0; i < dirlist.length; i++) {
                    if (dirlist[i].equals(fileToRead)) {
                        Path filePath = Paths.get("Files", fileToRead);
                        File tempfile = new File(filePath.toString());
                        File emptyFile= new File("Files");
                        while(tempfile.compareTo(emptyFile)!=0)
                         ans=countAndDivideBytes(tempfile);
                    }
            }
        }
        else
            ans= getError(0, ""); //file not found for reading

        return ans;
    }
    private Packet WRQhandle(Packet tmp) {
        fileToWrite = ((RRQandWRQ) tmp).getFileName();
        if (!searchTheFileInFolder(fileToWrite)) {
                firstWriteFlag = false;
                tmp.setFinished();
                return checkACK(0, false);
        }
        else {
            tmp.setFinished();
            return getError(5, ""); //file already exist
        }
    }

    private Packet DataHandle(Packet tmp) {
        byte[] byteArray= ((DATA) tmp).data;

        for (int i = 0; i < byteArray.length; i++)
            singleFileData.add(byteArray[i]);

        int tmpCount=writeBlockingCount;
        writeBlockingCount++;

        if (((DATA) tmp).packetSize< 512) {
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
                writeHasFinished=true;
                writeBlockingCount=1;
            }
        }
            return checkACK(tmpCount, true);
    }


    private ACK AckHandle(Packet tmp) {
        if (moreDataNeeded) {
           dataGotAck=true;
            return checkACK(((DATA) tmp).blockNum, true);
        } else {
            return checkACK(0, false);
        }
    }
    private ERROR ErrorHandle(Packet tmp) {
        return getError(((ERROR) tmp).errorCode, ((ERROR) tmp).errMsg);
    }
    private Packet DirqHandle(Packet tmp) {
        String allFilesNames = "";
        String textPath="Files" + File.separator;
        File folder = new File("Files");

        File[]allFilesArr= folder.listFiles();
        for(File file:allFilesArr ){
            allFilesNames+= (file.getName() + '\0');
        }

        if (allFilesNames.length()==0)
            return getError(0, "No Files to show");
        else{
            allFilesNames+='0';
            byte[]fileNamesBytes= allFilesNames.getBytes();
            short sizeOfByteArr=(short)fileNamesBytes.length;
            DATA ans= new DATA((short)03, sizeOfByteArr,(short)1,fileNamesBytes);
            return ans;
        }
    }
private Packet LogrqHandle(Packet tmp) {
    String username = ((LOGRQ) tmp).username;
    if (!isLogged) {
        if (loggedUsers.contains(username)) {
            return getError(7, ""); //user already logged in
        } else {
            if (!connections.MyConnections.contains(ID)) {
                isLogged = true;
                loggedUsername=username;
                loggedUsers.add(loggedUsername);
                return checkACK(0, false);
            }
            return getError(0, "");
        }
    }
    return getError(7, "");
}

    private boolean removeFromFilesFolder(String deleteMe) {
        try {
            Path p1 = Paths.get("Files"+ File.separator + deleteMe);
            Files.delete(p1);
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
    private boolean searchTheFileInFolder(String findMe){

        File curDir = new File("Files"+ File.separator);
        File[] filesList = curDir.listFiles();
        for(File f : filesList){
            if(f.getName().equals(findMe)) {
                return true;
               }
        }
    return false;
    }

    private Packet DelrqHandle(Packet tmp) {
        String filenameToDel = ((DELRQ) tmp).filename;
        if (searchTheFileInFolder(filenameToDel)) {
            removeFromFilesFolder(filenameToDel);
            connections.send(ID,checkACK(0, false));
            BCAST b = new BCAST((short)9, (byte) 0,filenameToDel);
            isBcast=true;
            return b;
        } else
            return getError(1, ""); //cannot read violation
    }


    private Packet BcastHandle(Packet tmp) {
        connections.broadcast(((BCAST) tmp));
        isBcast = true;
        return null;
    }

    private ACK DiscHandle(Packet tmp) {
        loggedUsers.remove(loggedUsername);
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