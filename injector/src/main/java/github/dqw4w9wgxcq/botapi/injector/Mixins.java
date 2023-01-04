package github.dqw4w9wgxcq.botapi.injector;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.Socket;
import java.util.function.BiFunction;

@Slf4j
public class Mixins {
    @Setter
    private static BiFunction<InetAddress, Integer, Socket> socketFactory = null;

    public static Socket createSocket(InetAddress address, int port) {
        log.info("createSocket: {} {}", address, port);

        if (socketFactory == null) {
            log.error("socketFactory is null");
            //if the game fails to connect on 43594, it will try 443.
            //openosrs names are wrong, it has nothing to do with js5/world.  both ports will be tried for either
            //all exceptions/errors are swallowed because it runs on a separate thread.  throwing does not error the game.
            //if the thread dies, TaskHandler status is set to 2 which causes an IOException in the main thread
            //the IOE gets caught and then the ports are swapped
            //		} catch (IOException var26) {
            //			if (field526 < 1) {
            //				if (class159.worldPort == class454.currentPort) {
            //					class454.currentPort = class131.js5Port;
            //				} else {
            //					class454.currentPort = class159.worldPort;
            //				}
            //				++field526;
            //				ReflectionCheck.method713(0);
            //			} else {
            //				class129.getLoginError(-2);
            //			}
            //		}
            throw new RuntimeException("socketFactory is null");//gets swallowed
        }

        try {
            return socketFactory.apply(address, port);
        } catch (Exception e) {
            log.error("socketFactory.apply failed", e);
            throw new RuntimeException("socketFactory.apply failed", e);//gets swallowed
        }
    }
}
