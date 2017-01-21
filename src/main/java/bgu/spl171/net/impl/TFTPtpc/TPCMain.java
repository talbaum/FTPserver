package bgu.spl171.net.impl.TFTPtpc;

import bgu.spl171.net.impl.TFTPtpc.srv.Reactor;
import bgu.spl171.net.impl.TFTPtpc.srv.ThreadPerClient;

import java.io.IOException;

/**
 * Created by baum on 17/01/2017.
 */
public class TPCMain {
    public static void main(String[] args) throws IOException {

          Reactor r = new Reactor(2, 8888, () -> {
              return new TFTPprotocol();
          }, () -> {
              return new EncodeDecodeIMP();
          });
          ThreadPerClient s = new ThreadPerClient(8888, () -> {
              return new TFTPprotocol();
          }, () -> {
              return new EncodeDecodeIMP();
          });

        //   s.serve();
         //  s.close();
          r.serve();
          r.close();
      }
    }

