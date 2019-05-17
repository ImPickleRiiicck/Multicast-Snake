package com.csc445.shared.packets;

import com.csc445.shared.game.Spot;

import java.net.DatagramPacket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class PlayPacket extends Packet {

    private final Spot spot;

    private short sequenceNumber;

    public PlayPacket() { // used for incoming play
        super(Type.PLAY);
        spot = new Spot();
    }

    public PlayPacket(Spot spot) {
        super(Type.PLAY);
        this.spot = spot;
        this.sequenceNumber = -1;
    }

    public PlayPacket(Spot spot, short sequenceNumber) {
        super(Type.PLAY);
        this.spot = spot;
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public void parseSocketData(DatagramPacket packet) {
        final byte[] data = packet.getData();

        final byte[] p = Arrays.copyOfRange(packet.getData(), 1, 3);
        sequenceNumber = ByteBuffer.wrap(p).getShort();

        spot.setX(data[3]);
        spot.setY(data[4]);
        spot.setColor(data[5]);
        spot.setName(new String(data, 6, packet.getLength() - 6));
    }

    @Override
    protected void createPacketData() {
        addData(getType().getValue());
        addByteArray(shortToByteArray(sequenceNumber));

        for (byte b : spot.getByteArray()) {
            addData(b);
        }
    }

    public short getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(short sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public Spot getSpot() {
        return spot;
    }
}
