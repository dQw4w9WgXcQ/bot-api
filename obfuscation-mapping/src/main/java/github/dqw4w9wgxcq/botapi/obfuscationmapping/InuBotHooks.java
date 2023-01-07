package github.dqw4w9wgxcq.botapi.obfuscationmapping;

import com.google.gson.Gson;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.util.List;

@Data
public class InuBotHooks {
    private static InuBotHooks inuBotHooks = null;

    @SneakyThrows
    public static InuBotHooks get() {
        if (inuBotHooks == null) {
            String json;
            try (InputStream is = InuBotHooks.class.getResourceAsStream("/inubothooks.json")) {
                if (is == null) {
                    throw new RuntimeException("inubothooks.json not found");
                }
                json = new String(is.readAllBytes());
            }
            inuBotHooks = new Gson().fromJson(json, InuBotHooks.class);
        }
        return inuBotHooks;
    }

    private final int revision;
    private final int hash;
    private final List<Class> classes;

    @Data
    public static class Class {
        private final String definition;
        private final List<Field> fields;
        private final List<String> breaks;
    }

    @Data
    public static class Field {
        private final String definition;
        private final String owner;
        private final String name;
        private final String descriptor;
        private final long multiplier;
        private final boolean virtual;
    }

    @Data
    public static class Method {
        private final String definition;
        private final String owner;
        private final String name;
        private final String descriptor;
        private final String expectedDescriptor;
        private final long predicate;
        private final boolean virtual;
    }
}
