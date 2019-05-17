package com.csc445.shared.packets;

import com.csc445.shared.utils.AES;
import com.csc445.shared.utils.Constants;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

public abstract class Packet {

    private List<Byte> data = new ArrayList<>(Constants.PACKET_SIZE);
    private final Type type;

    Packet(Type type) {
        this.type = type;
    }

    public abstract void parseSocketData(DatagramPacket packet); // always start at 1 because we already determined what type of packet it was

    protected abstract void createPacketData();

    public byte[] createPacket() {
        createPacketData();
        return arrayListToArrayHelper();
    }

    private byte[] arrayListToArrayHelper() {
        final byte[] dataArray = new byte[data.size()];

        for (int i = 0; i < dataArray.length; i++) {
            dataArray[i] = data.get(i);
        }

        return dataArray;
    }

    byte[] shortToByteArray(short number) {
        return ByteBuffer.allocate(2).putShort(number).array();
    }

    void addByteArray(byte[] data) {
        for (byte b : data) {
            addData(b);
        }
    }

    void addData(byte data) {
        this.data.add(data);
    }

    Type getType() {
        return type;
    }

    protected enum Type {
        JOIN((byte) 1),
        PLAY((byte) 2),
        MESSAGE((byte) 5),
        STATE_REQ((byte) 6),
        ERROR((byte) 7);

        private final byte value;

        Type(byte value) {
            this.value = value;
        }

        public byte getValue() {
            return this.value;
        }
    }
}
