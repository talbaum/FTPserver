package bgu.spl171.net.impl.TFTPtcp.srv;

import java.util.function.Supplier;

/**
 * Created by bauum on 17/01/2017.
 */
public class ThreadPerClient extends BaseServer {

    public ThreadPerClient(int port, Supplier protocolFactory, Supplier encdecFactory) {
        super(port, protocolFactory, encdecFactory);

    }
    @Override
    protected void execute(BlockingConnectionHandler handler) {
        new Thread(handler).start();
    }
}
