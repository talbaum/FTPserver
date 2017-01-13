package bgu.spl171.net.srv;

import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.Connections;
import bgu.spl171.net.packets.*;


import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by baum on 10/01/2017.
 */
public class TFTPprotocol<T> implements BidiMessagingProtocol<T> {
    int ID;
    ConnectionsImpl connections;
    ConcurrentHashMap<String, LinkedList<Byte>> files = new ConcurrentHashMap<>();
    LinkedList<Byte> singleFileData = new LinkedList<>();
    boolean isBcast=false;
    String fileToWrite;

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connections = (ConnectionsImpl) connections;
        this.ID = connectionId;

    }

    @Override
    public void process(T message) {
        Packet tmp = (Packet) message;
        short OP = tmp.getOpcode();
        byte[] ans=null;

        switch (OP) {
            case 1:
                //need to read file
                String fileToRead=((RRQandWRQ) message).getFileName();
                if (files.contains(fileToRead))
                    ans=read(fileToRead);
                else
                    ans = getError(5, "");

                break;

            case 2:
                fileToWrite = ((RRQandWRQ) message).getFileName();
                if (!files.contains(fileToWrite)) {
                    if (byteToFile(((RRQandWRQ) tmp).encode())) {
                        files.put(fileToWrite,null);
                        ans = checkACK(0, false);
                        break;
                    } else
                        ans = getError(0, ""); //unknown error
                        break;
                } else
                    ans = getError(5, ""); //file already exist
                break;

            case 3:
                //need to check how to receive few blocks untill the data is finished
                String letAllKnow=null;
                byte[] byteArray = ((DATA) tmp).data;
                for (int i = 0; i < byteArray.length; i++)
                    singleFileData.add(byteArray[i]);

                if (((DATA) tmp).packetSize < 512) {
                    files.replace(fileToWrite,null,singleFileData);
                    byteArray = new byte[singleFileData.size()];
                    int i = 0;
                    while (!singleFileData.isEmpty()) {
                        byteArray[i] = singleFileData.pollFirst();
                        i++;
                    }
                    if (!byteToFile(byteArray))
                    {
                        ans = getError(2, "");
                        break;
                    }
                    else{
                        letAllKnow=fileToWrite+" has completed uploading to the server.";
                                connections.broadcast(letAllKnow);
                    }
                }
                if(letAllKnow==null)
                ans = checkACK(((DATA) tmp).blockNum, true);

                break;

            case 4:
                if(tmp.getOpcode()==3){
                    //this is a data block, can send another block of data
                    ans=checkACK( ((DATA)tmp).blockNum , true);
                }
                else{
                    ans= checkACK(0,false);
                }
                break;

            case 5:
            ans= getError(((ERROR)tmp).errorCode ,  ((ERROR)tmp).errMsg);
            break;

            case 6:
                String allFilesNames="";
                for (String nameOfFile : files.keySet()) {
                    allFilesNames+= nameOfFile + " \0 ";
                }
                if(allFilesNames.equals(""))
                    ans=getError(0,"No Files to show");
                else
                    ans= allFilesNames.getBytes();
                break;

            case 7:
                String username= ((LOGRQ)tmp).username;
               if(!connections.MyConnections.contains(username)){
                    connections.MyConnections.put(ID,username);
                    ans= checkACK(0,false);
                }
                else
                    ans = getError(7, ""); //user already logged in

                break;

            case 8:
                String filenameToDel=((DELRQ)tmp).filename;
                if(files.contains(filenameToDel)) {
                    files.remove(filenameToDel);
                    //need to actual delete the data from the server
                    ans = checkACK(0, false);
                }
                else
                    ans=getError(1,"");
                break;

            case 9:
                connections.broadcast(((BCAST) tmp).encode());
                isBcast=true;
                break;

            case 10:
                connections.disconnect(ID);
                ans= checkACK(0,false);
                break;
        }

        if(!isBcast)
            connections.send(ID, ans);
        else
            isBcast=false;
    }


    @Override
    public boolean shouldTerminate() {
        return false;
    }

    private byte[] checkACK(int blockNum, boolean isData) {
        if (!isData)
            return "ACK 0".getBytes();
        else {
            return ("ACK " + blockNum).getBytes();
        }
    }

    private byte[] getError(int errorCode, String errorMsg) {
        switch (errorCode) {
            case 0:
                return (5 + "Not defined, see error message (if any). " + errorMsg).getBytes();
            case 1:
                return (5 + "File not found – RRQ of non-existing file  " + errorMsg).getBytes();
            case 2:
                return (5 + "Access violation – File cannot be written, read or deleted.   " + errorMsg).getBytes();
            case 3:
                return (5 + "Disk full or allocation exceeded – No room in disk " + errorMsg).getBytes();
            case 4:
                return (5 + "Illegal TFTP operation – Unknown Opcode  " + errorMsg).getBytes();
            case 5:
                return (5 + "File already exists – File name exists on WRQ.  " + errorMsg).getBytes();
            case 6:
                return (5 + "User not logged in – Any opcode received before Login completes.  " + errorMsg).getBytes();
            case 7:
                return (5 + "User already logged in – Login username already connected.  " + errorMsg).getBytes();
        }
        return "".getBytes();
    }

    private boolean byteToFile(byte[] tmp) {
        try {
            FileOutputStream fos = new FileOutputStream("\"C:\\\\Users\\\\באום\\\\Desktop\\\\SPL\\\\Intelij Projects\\\\SPL3\\\\net\\\\src\\\\main\\\\java\\\\bgu\\\\spl171\\\\net\\\\srv\\\\Files\"");
            fos.write(tmp);
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private byte[] read(String readMe){
        byte[] arr= new byte[512];
       LinkedList<Byte> readedFile= files.get(readMe);
       int i=0;
       while(i<512 && !readedFile.isEmpty())
       {
           arr[i]=readedFile.pollFirst();
            i++;
       }
       return arr;
    }

}