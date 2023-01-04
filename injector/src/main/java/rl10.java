import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class rl10 {
    public static Socket socket(InetAddress address, int port) {
        System.out.println("rl10.socket(" + address + ", " + port + ")");
        try {
            return new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
