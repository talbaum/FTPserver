package bgu.spl171.net.srv;

import bgu.spl171.net.api.BidiMessagingProtocol;
import bgu.spl171.net.api.ConnectionsImpl;
import bgu.spl171.net.api.MessageEncoderDecoder;
import bgu.spl171.net.api.TFTPprotocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Supplier;

public abstract class BaseServer<T> implements Server<T> {

    private final int port;
    private final Supplier<BidiMessagingProtocol<T>> protocolFactory;
    private final Supplier<MessageEncoderDecoder<T>> encdecFactory;
    private ServerSocket sock;
    public ConnectionsImpl<T> connections1;

    public BaseServer(
            int port,
            Supplier<BidiMessagingProtocol<T>> protocolFactory,
            Supplier<MessageEncoderDecoder<T>> encdecFactory) {
        this.port = port;
        this.protocolFactory = protocolFactory;
        this.encdecFactory = encdecFactory;
		this.sock = null;
		this.connections1 = new ConnectionsImpl<T>();
        ((TFTPprotocol<T>)protocolFactory.get()).connections= connections1;
    }

    @Override
    public void serve() {

        try (ServerSocket serverSock = new ServerSocket(port)) {

            this.sock = serverSock; //just to be able to close

            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("Waiting for connection bro...");
                Socket clientSock = serverSock.accept();
                System.out.println("YES! got accepted");
                BlockingConnectionHandler<T> handler = new BlockingConnectionHandler<>(
                        clientSock,
                        encdecFactory.get(),
                        protocolFactory.get());
                connections1.addConnection(handler);
                System.out.println("Before Execute");
                execute(handler);
                System.out.println("After Execute");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        System.out.println("server closed!!!");
    }

    @Override
    public void close() throws IOException {
		if (sock != null)
			sock.close();
    }

    protected abstract void execute(BlockingConnectionHandler<T>  handler);

}
