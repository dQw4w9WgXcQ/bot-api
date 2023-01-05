package github.dqw4w9wgxcq.botapi.mixins;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class SocketMixins {
    public interface SocketFactory {
        Socket createSocket(InetAddress address, int port, Object task);
    }

    @Setter
    private static SocketFactory socketFactory = null;

    /*
    if the game/js5 fails to connect on 43594, it will try 443.
    openosrs names are wrong, it has nothing to do with js5/world.  both ports will be tried for either
    all exceptions/errors are swallowed because socket creation happens on a separate thread.  throwing does not error the game, only sets the Task status to 2
    task status 2 causes new IOException(); on the main thread
    the IOE gets caught and then the ports are swapped
    		} catch (IOException var26) {
    			if (field526 < 1) {
    				if (class159.worldPort == class454.currentPort) {
    					class454.currentPort = class131.js5Port;
    				} else {
    					class454.currentPort = class159.worldPort;
    				}
    				++field526;
    				ReflectionCheck.method713(0);
    			} else {
    				class129.getLoginError(-2);
    			}
    		}
     */
    //returning null will crash the game with NPE
    @SneakyThrows
    public static Socket createSocket(InetAddress address, int port, Object task) {
        log.info("createSocket: {} {} {}", address, port, task);

        if (socketFactory == null) {
            log.error("socketFactory not set");
            throw new RuntimeException();//gets swallowed by Task thread
        }

        return socketFactory.createSocket(address, port, task);
    }
}
