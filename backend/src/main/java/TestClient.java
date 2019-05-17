import com.csc445.shared.game.Spot;
import com.csc445.shared.packets.PlayPacket;
import com.csc445.shared.utils.AES;
import com.csc445.shared.utils.Constants;

import java.io.IOException;
import java.net.*;
import java.security.InvalidKeyException;

public class TestClient {

    public static void main(String[] args) throws IOException, InvalidKeyException {
        System.setProperty("java.net.preferIPv4Stack", "true"); // This is needed because OSX seems to return an IPv6 address and it does not work with IPv6 for some reason
        final MulticastSocket socket = new MulticastSocket(Constants.GROUP_PORT);
        final InetAddress group = InetAddress.getByName(Constants.GROUP_ADDRESS);
        socket.joinGroup(group);
        final InetAddress server = InetAddress.getByName("127.0.0.1");

        System.out.println("Test client started...");
        System.out.println(group);

        String secretKey = AES.TEST_PASSWORD;

        final Spot s = new Spot(1, 2);
        s.setName("Landon");
        final PlayPacket playPacket = new PlayPacket(s, (short) 738);
        final byte[] buf = playPacket.createPacket();
        final byte[] encrypt = AES.encryptByteArray(buf, buf.length, secretKey);
        socket.send(new DatagramPacket(encrypt, encrypt.length, server, Constants.SERVER_PORT));
    }
}
