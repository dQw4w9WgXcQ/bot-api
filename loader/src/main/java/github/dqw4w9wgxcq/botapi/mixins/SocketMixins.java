package github.dqw4w9wgxcq.botapi.mixins;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Field;
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
    //@param task is used to determine if the socket is for the game or js5
    public static Socket createSocket(InetAddress address, int port, Object task) {
        log.info("createSocket: {} {} {}", address, port, task);

        try {
            String js5SocketTaskOwnerClassName = "mj";
            String js5SocketTaskFieldName = "ev";

            Class<?> js5SocketTaskOwnerClass = Class.forName(js5SocketTaskOwnerClassName, false, task.getClass().getClassLoader());
            Field js5SocketTaskField = js5SocketTaskOwnerClass.getDeclaredField(js5SocketTaskFieldName);
            js5SocketTaskField.setAccessible(true);
            Object js5SocketTask = js5SocketTaskField.get(null);

            if (js5SocketTask == task) {
                log.info("createSocket: js5");
                if (js5SocketFactory == null) {
                    throw new IllegalStateException("js5SocketFactory is null");
                }

                return js5SocketFactory.createSocket(address, port, task);
            }

            Class<?> gameSocketTaskOwnerClass = Class.forName("t", false, task.getClass().getClassLoader());
            Field gameSocketTaskField = gameSocketTaskOwnerClass.getDeclaredField("hi");
            gameSocketTaskField.setAccessible(true);
            Object gameSocketTask = gameSocketTaskField.get(null);

            if (gameSocketTask == task) {
                log.info("createSocket: game");
                if (gameSocketFactory == null) {
                    throw new IllegalStateException("gameSocketFactory is null");
                }

                return gameSocketFactory.createSocket(address, port, task);
            }

            throw new IllegalStateException("task: " + task + " is not js5SocketTask: " + js5SocketTask + " or gameSocketTaskRs: ");
        } catch (Throwable t) {
            //noinspection ConstantValue
            if (t instanceof IOException) {//sneaky catch
                log.warn("createSocket: IOException", t);
                throw new RuntimeException(t);
            }

            log.error("createSocket", t);
            System.exit(200);
            return null;//unreachable
        }
    }
}
