package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.EncodeDecodeIMP;
import bgu.spl171.net.impl.TFTPprotocol;
import bgu.spl171.net.impl.srv.Reactor;
import bgu.spl171.net.impl.srv.ThreadPerClient;

import java.io.IOException;

/**
 * Created by baum on 17/01/2017.
 */
public class TPCMain {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);

          ThreadPerClient s = new ThreadPerClient(port, () -> {
              return new TFTPprotocol();
          }, () -> {
              return new EncodeDecodeIMP();
          });

          s.serve();
          s.close();
      }
    }

