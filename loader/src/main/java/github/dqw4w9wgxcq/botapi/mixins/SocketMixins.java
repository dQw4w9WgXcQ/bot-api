package github.dqw4w9wgxcq.botapi.mixins;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class SocketMixins {
    public interface SocketFactory {
        Socket createSocket(InetAddress address, int port, Object taskRs);
    }

    @Setter
    private static SocketFactory js5SocketFactory = null;
    @Setter
    private static SocketFactory gameSocketFactory = null;

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
    //taskRs is used to determine if the socket is for the game or js5
    public static Socket createSocket(InetAddress address, int port, Object taskRs) {
        log.info("createSocket: {} {} {}", address, port, taskRs);

        if (js5SocketFactory == null) {
            log.error("js5SocketFactory not set");
            System.exit(205);
        }

        return js5SocketFactory.createSocket(address, port, taskRs);
    }
}
