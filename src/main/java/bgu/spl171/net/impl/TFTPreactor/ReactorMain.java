package bgu.spl171.net.impl.TFTPreactor;

import bgu.spl171.net.impl.EncodeDecodeIMP;
import bgu.spl171.net.impl.TFTPprotocol;
import bgu.spl171.net.impl.srv.Reactor;
import bgu.spl171.net.impl.srv.ThreadPerClient;

import java.io.IOException;

/**
 * Created by amitu on 21/01/2017.
 */
public class ReactorMain {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);

        Reactor r = new Reactor(2, port, () -> {
            return new TFTPprotocol();
        }, () -> {
            return new EncodeDecodeIMP();
        });

        r.serve();
            r.close();
    }
}
