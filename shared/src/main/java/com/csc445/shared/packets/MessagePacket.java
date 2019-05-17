package com.csc445.shared.packets;

import java.net.DatagramPacket;

public class MessagePacket extends Packet {

    private String message;

    public MessagePacket() { // used for incoming message
        super(Type.MESSAGE);
    }

    public MessagePacket(String message) {
        super(Type.MESSAGE);
        this.message = message;
    }

    @Override
    public void parseSocketData(DatagramPacket packet) {
        message = new String(packet.getData(), 1, packet.getLength() - 1);
    }

    @Override
    protected void createPacketData() {
        addData(getType().getValue());

        for (byte b : message.getBytes()) {
            addData(b);
        }
    }

    public String getMessage() {
        return message;
    }
}
