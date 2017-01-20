package bgu.spl171.net.srv;

import bgu.spl171.net.api.ConnectionsImpl;
import bgu.spl171.net.api.EncodeDecodeIMP;
import bgu.spl171.net.api.TFTPprotocol;

import java.io.IOException;

/**
 * Created by baum on 17/01/2017.
 */
public class Main {
    public static void main(String[] args) throws IOException {
      /*  //lambda has replaced the new Supplier<BidiMeseggingProtocol>()

        Supplier<BidiMessagingProtocol> sp= () -> {
            TFTPprotocol tp = new TFTPprotocol();
            return tp;
        };
        Supplier<bgu.spl171.net.srv.MessageEncoderDecoder> sp2= () -> {
            EncodeDecodeIMP ed= new EncodeDecodeIMP();
            return ed;
        };

        ThreadPerClient t= new ThreadPerClient(8888,sp,sp2);
        Reactor r= new Reactor(2,8888,sp,sp2);
        r.serve();
        r.close();

        //t.serve();
        //t.close();
*/
        Reactor r= new Reactor(2,8888,()->{return new TFTPprotocol();},()->{return new EncodeDecodeIMP();});
        ThreadPerClient s = new ThreadPerClient(8888, () -> {
            return new TFTPprotocol();
        }, () -> {
            return new EncodeDecodeIMP();
        });

       // s.serve();
        //s.close();
        r.serve();
        r.close();

    }
}
