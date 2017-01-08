package bgu.spl171.net.impl;

import bgu.spl171.net.api.MessageEncoderDecoder;

/**
 * Created by amitu on 08/01/2017.
 */
public class EncodeDecodeIMP implements MessageEncoderDecoder<T> {
    @Override
    public T decodeNextByte(byte nextByte) {
        return null;
    }

    @Override
    public byte[] encode(T message) {
        return new byte[0];
    }
}
