package github.dqw4w9wgxcq.botapi.loader;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ScriptThread extends Thread {
    @Getter
    private volatile IBotScript activeScript;

    public ScriptThread() {
        super("script");
    }

    @SneakyThrows
    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (this) {
                if (activeScript == null) {
                    log.info("waiting for script");

                    wait();
                }
            }

            try {
                activeScript.run();
            } catch (Exception e) {
                log.warn("exception in script run", e);
            } catch (Error e) {//just catch all errors bc kotlin t0do error etc.
                if (e instanceof VirtualMachineError) {//actual bad error
                    throw e;
                }

                //probably recoverable error (kotlin t0d0 error etc.)
                log.warn("error in script run", e);
            }

            activeScript = null;
        }
    }

    public boolean offer(IBotScript script) {
        synchronized (this) {
            if (!isAlive()) {
                throw new IllegalStateException("script thread dead");
            }

            if (activeScript != null) {
                return false;
            }

            activeScript = script;
            notify();
            return true;
        }
    }
}
