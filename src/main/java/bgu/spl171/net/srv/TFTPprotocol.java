package bgu.spl171.net.srv;

import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.Connections;

import java.util.Vector;

/**
 * Created by baum on 10/01/2017.
 */
public class TFTPprotocol<T> implements BidiMessagingProtocol<T> {
    //T response=null;
    int ID;
    ConnectionsImpl Connections1;
    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.Connections1=(ConnectionsImpl)connections;
        this.ID=connectionId;
    }

    @Override
    public void process(T message) {
        Packet tmp = (Packet)message;
        short OP = tmp.getOpcode();
        byte[] ans;

        switch (OP){
            case 1:

            case 2:

            case 3:

            case 4:
                break;

            case 5:

            case 6:
                break;

            case 7:

            case 8:

            case 9:
                BCAST tmp1 = (BCAST)tmp;
                Vector<Byte> byteVector = tmp1.getByteVector();
                byte[] bytes = new byte[byteVector.size()];
                for (int i=0;i<bytes.length;i++){
                    bytes[i]=byteVector.get(i);
                }
                ans = bytes;
                Connections1.broadcast(ans);

            case 10:
                Connections1.disconnect(ID);
        }
    Connections1.send(ID,message);
    }

    @Override
    public boolean shouldTerminate() {
        return false;
    }
}