package github.dqw4w9wgxcq.botapi.obfuscationmapping;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InuBotHooksTest {

    @Test
    void init() {

        InuBotHooks.init();
        Assertions.assertNotNull(InuBotHooks.get());
    }
}