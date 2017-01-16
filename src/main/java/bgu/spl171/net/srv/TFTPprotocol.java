package bgu.spl171.net.srv;
import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.Connections;


import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
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
    String fileToWrite;

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
        if (!isData)
            return "ACK 0".getBytes();
        else {
            return ("ACK " + blockNum).getBytes();
        }
    }

    private byte[] getError(int errorCode, String errorMsg) {
        switch (errorCode) {
            case 0:
                return ("Not defined, see error message (if any). " + errorMsg).getBytes();
            case 1:
                return ("File not found – RRQ of non-existing file  " + errorMsg).getBytes();
            case 2:
                return ("Access violation – File cannot be written, read or deleted.   " + errorMsg).getBytes();
            case 3:
                return ("Disk full or allocation exceeded – No room in disk " + errorMsg).getBytes();
            case 4:
                return ("Illegal TFTP operation – Unknown Opcode  " + errorMsg).getBytes();
            case 5:
                return ("File already exists – File name exists on WRQ.  " + errorMsg).getBytes();
            case 6:
                return ("User not logged in – Any opcode received before Login completes.  " + errorMsg).getBytes();
            case 7:
                return ("User already logged in – Login username already connected.  " + errorMsg).getBytes();
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

    private byte[] read(String readMe) {
        byte[] arr = new byte[512];
        LinkedList<Byte> readedFileBytes = files.get(readMe);
        int i = 0;
        while (i < 512 && !readedFileBytes.isEmpty()) {
            arr[i] = readedFileBytes.pollFirst();
            i++;
        }
        if (readedFileBytes.isEmpty())
            removeFromFilesFolder(readMe);

        return arr;
    }

    private boolean removeFromFilesFolder(String deleteMe) {
        try {
            Path p1 = Paths.get("C:\\Users\\באום\\Desktop\\SPL\\Intelij_Projects\\SPL3\\net\\src\\main\\java\\bgu\\spl171\\net\\srv\\Files\\" + deleteMe);
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

    private byte[] RRQhandle(Packet tmp) {
        String fileToRead = ((RRQandWRQ) tmp).getFileName();
        if (files.containsKey(fileToRead)) {
            if (files.get(fileToRead).isEmpty()) {
                String letAllKnowRead = fileToRead + " has completed uploading to the server.";
                connections.broadcast(letAllKnowRead.getBytes());
                isBcast=true;
                return null;
            } else {
                return read(fileToRead);
            }
        } else
            return getError(1, ""); //file not found for reading
    }

    private byte[] WRQhandle(Packet tmp) {
        fileToWrite = ((RRQandWRQ) tmp).getFileName();
        if (!files.containsKey(fileToWrite)) {
            if (byteToFile(((RRQandWRQ) tmp).encode())) { //need to make sure that
                files.put(fileToWrite, EMPTYLIST);
                return checkACK(0, false);
            } else
                return getError(2, ""); //file cannot be written error
        } else
            return getError(5, ""); //file already exist
    }
    private byte[] DataHandle(Packet tmp) {
        String letAllKnow = null;
        byte[] byteArray = ((DATA) tmp).data;

        for (int i = 0; i < byteArray.length; i++)
            singleFileData.add(byteArray[i]);

        if (((DATA) tmp).packetSize < 512) {
            files.replace(fileToWrite, EMPTYLIST, singleFileData);
            byteArray = new byte[singleFileData.size()];

            int i = 0;
            while (!singleFileData.isEmpty()) {
                byteArray[i] = singleFileData.pollFirst();
                i++;
            }

            if (!byteToFile(byteArray)) {
                return getError(2, ""); //cannot write error
            } else {
                letAllKnow = fileToWrite + " has completed uploading to the server.";
                connections.broadcast(letAllKnow.getBytes());
                isBcast=true;
                return null;
            }
        }
        if (letAllKnow == null)
            return checkACK(((DATA) tmp).blockNum, true);
    else
        return null;
    }

    private byte[] AckHandle(Packet tmp) {
        if (tmp.getOpcode() == 3) {
            //this is a data block, can send another block of data
            return checkACK(((DATA) tmp).blockNum, true);
        } else {
            return checkACK(0, false);
        }
    }
    private byte[] ErrorHandle(Packet tmp)
    {
        return getError(((ERROR) tmp).errorCode, ((ERROR) tmp).errMsg);
    }
    private byte[] DirqHandle(Packet tmp) {
        String allFilesNames = "";
    for (String nameOfFile : files.keySet()) {
        allFilesNames += nameOfFile + " \0 ";
    }

    if (allFilesNames.equals(""))
        return getError(0, "No Files to show");
    else
        return allFilesNames.getBytes();
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


}