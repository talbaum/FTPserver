package bgu.spl171.net.impl.TFTPtpc.srv;

import bgu.spl171.net.impl.TFTPtpc.api.*;
import bgu.spl171.net.impl.TFTPtpc.ConnectionsImpl;
import bgu.spl171.net.impl.TFTPtpc.TFTPprotocol;
import bgu.spl171.net.impl.TFTPtpc.packets.Packet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private ConnectionsImpl<T> connections;

    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, ConnectionsImpl<T> cons) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        ((TFTPprotocol<T>)this.protocol).connections=cons;
        this.connections=cons;
    }

    @Override
    public void run() {
        try (Socket sock = this.sock) { //just for automatic closing
            int read;

            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());

            while (connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null && ((Packet)nextMessage).isFinished()) {
                    protocol.process(nextMessage);
                }
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    @Override
    public void send(T msg) {
        try {
            byte[]tmp=encdec.encode(msg);
            out.write(tmp);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
