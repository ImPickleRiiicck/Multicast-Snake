package com.csc445.frontend.Utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.csc445.frontend.Stage.GameStage;
import com.csc445.shared.game.Spot;
import com.csc445.shared.packets.ErrorPacket;
import com.csc445.shared.packets.MessagePacket;
import com.csc445.shared.packets.PlayPacket;
import com.csc445.shared.utils.AES;
import com.csc445.shared.utils.Constants;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.security.InvalidKeyException;

public class Helper {

    private static final Color[] COLORS = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE, Color.WHITE, Color.BLACK, Color.PINK, Color.TEAL};

    private static Color selectedColor = Color.BLACK;
    private static byte selectedColorByte = 7; // black

    private static MulticastSocket socket;

    static {
        try {
            socket = new MulticastSocket(Constants.GROUP_PORT);
            socket.joinGroup(InetAddress.getByName(Constants.GROUP_ADDRESS));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void sendPacket(byte[] data, InetAddress address, int port) {
        try {
            final byte[] encryptedDataArray = AES.encryptByteArray(data, data.length, State.getSecretKey());
            if (encryptedDataArray != null) {
                socket.send(new DatagramPacket(encryptedDataArray, encryptedDataArray.length, address, port));
            }
        } catch (IOException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    public static void receivePackets(GameStage stage) {
        new Thread(() -> {
            while (true) {
                try {
                    final byte[] buf = new byte[512];
                    final DatagramPacket receivedPacket = new DatagramPacket(buf, buf.length);

                    socket.receive(receivedPacket); // we actually receive the data here

                    if (State.getSecretKey() != null) { // if they don't have the password, don't even try to decrypt packets
                        final byte[] decryptedData = decryptPacket(receivedPacket);

                        if (decryptedData == null) {
                            processWrongPassword();
                        } else {
                            receivedPacket.setData(decryptedData);
                            switch (receivedPacket.getData()[0]) {
                                case 2: // Play packet
                                    processPlayPacket(receivedPacket, stage);
                                    break;
                                case 5: // Message packet
                                    processMessagePacket(receivedPacket, stage);
                                    break;
                                case 7: // Error packet
                                    processErrorPacket(receivedPacket);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static byte[] decryptPacket(DatagramPacket packet) {
        try {
            return AES.decryptByteArray(packet.getData(), packet.getLength(), State.getSecretKey());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void processWrongPassword() {
        System.out.println("Cannot decrypt packet because the password is incorrect.");
    }

    private static void processPlayPacket(DatagramPacket packet, GameStage stage) {
        System.out.println("Play packet");

        final PlayPacket p = new PlayPacket();
        p.parseSocketData(packet);
        final Spot spot = p.getSpot();
        Gdx.app.postRunnable(() -> stage.updatePixel(spot.getX(), spot.getY(), spot.getColor(), spot.getName()));
        updateTextArea(stage, spot.getName() + " played " + Helper.convertByteToColor(spot.getColor()) + " at X: " + spot.getX() + ", Y: " + spot.getY());
    }

    private static void processMessagePacket(DatagramPacket packet, GameStage stage) {
        final MessagePacket m = new MessagePacket();
        m.parseSocketData(packet);
        updateTextArea(stage, m.getMessage());
    }

    private static void processErrorPacket(DatagramPacket packet) {
        final ErrorPacket e = new ErrorPacket();
        e.parseSocketData(packet);
        System.out.println(e.getErrorMessage());
    }

    private static void updateTextArea(GameStage stage, String message) {
        stage.getTextArea().appendText("\n" + message);
    }

    public static Color convertByteToColor(byte color) {
        if (color == 0) {
            return Color.RED;
        } else if (color == 1) {
            return Color.ORANGE;
        } else if (color == 2) {
            return Color.YELLOW;
        } else if (color == 3) {
            return Color.GREEN;
        } else if (color == 4) {
            return Color.BLUE;
        } else if (color == 5) {
            return Color.PURPLE;
        } else if (color == 6) {
            return Color.WHITE;
        } else if (color == 7) {
            return Color.BLACK;
        } else if (color == 8) {
            return Color.PINK;
        } else if (color == 9) {
            return Color.TEAL;
        }

        return null;
    }

    private static byte convertColorToByte(Color color) {
        if (color.equals(Color.RED)) {
            return 0;
        } else if (color.equals(Color.ORANGE)) {
            return 1;
        } else if (color.equals(Color.YELLOW)) {
            return 2;
        } else if (color.equals(Color.GREEN)) {
            return 3;
        } else if (color.equals(Color.BLUE)) {
            return 4;
        } else if (color.equals(Color.PURPLE)) {
            return 5;
        } else if (color.equals(Color.WHITE)) {
            return 6;
        } else if (color.equals(Color.BLACK)) {
            return 7;
        } else if (color.equals(Color.PINK)) {
            return 8;
        } else if (color.equals(Color.TEAL)) {
            return 9;
        }

        return 7;
    }

    public static void setSelectedColor(Color color) {
        Helper.selectedColor = color;
        Helper.selectedColorByte = convertColorToByte(color);
    }

    public static Color getSelectedColor() {
        return Helper.selectedColor;
    }

    public static byte getSelectedColorByte() {
        return Helper.selectedColorByte;
    }

    public static Color[] getColors() {
        return COLORS;
    }
}
