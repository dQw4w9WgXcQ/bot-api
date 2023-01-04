package github.dqw4w9wgxcq.botapi.injector;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class Mixins {
    public static Socket socket(InetAddress address, int port) {
        log.debug("socket: {} {}", address, port);
        try {
            return new Socket(address, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
