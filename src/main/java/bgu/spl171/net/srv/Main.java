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

