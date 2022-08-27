package github.dqw4w9wgxcq.botapi.loader;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ScriptThread extends Thread {
    @Getter
    private volatile IBotScript activeScript;

    public ScriptThread() {
        super("script");
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            synchronized (this) {
                if (activeScript == null) {
                    try {
                        log.info("waiting for script");
                        wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            try {
                activeScript.run();
            } catch (Exception e) {
                log.warn("exception in script run method", e);
            } catch (Error e) {//just catch all errors bc kotlin t0do error etc.
                if (e instanceof VirtualMachineError) {//actual bad error
                    log.debug("script thread got virtual machine error", e);
                    throw e;
                }

                //probably recoverable error (kotlin t0d0 error etc.)
                log.warn("error in script method", e);
            }

            activeScript = null;
        }
    }

    public boolean offer(IBotScript script) {
        synchronized (this) {
            if (activeScript != null) {
                return false;
            }

            activeScript = script;
            notify();
            return true;
        }
    }
}
