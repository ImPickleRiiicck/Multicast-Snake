package com.csc445.shared.packets;

import java.net.DatagramPacket;

public class ErrorPacket extends Packet {

    private String errorMessage;

    public ErrorPacket() {
        super(Type.ERROR);
    }

    public ErrorPacket(String errorMessage) {
        super(Type.ERROR);
        this.errorMessage = errorMessage;
    }

    @Override
    public void parseSocketData(DatagramPacket packet) {
        errorMessage = new String(packet.getData(), 1, packet.getLength() - 1);
    }

    @Override
    protected void createPacketData() {
        addData(getType().getValue());

        for (byte b : errorMessage.getBytes()) {
            addData(b);
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
