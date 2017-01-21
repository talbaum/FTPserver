package bgu.spl171.net.impl.TFTPtcp.api;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by baum on 08/01/2017.
 */
public interface Message {
    ConcurrentLinkedQueue <String> files=new ConcurrentLinkedQueue<>();
    ConcurrentLinkedQueue<String> loggedUsers= new ConcurrentLinkedQueue<>();
    LinkedList<Byte> singleFileData= new LinkedList<Byte>();

    short getOpCode();


}
