package service.hash;

import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import io.ktor.server.config.ApplicationConfig;

public class HashService {
    private final Argon2PasswordEncoder arg2SpringSecurity;

    public HashService(@NotNull ApplicationConfig applicationConfig) {
        int saltLength = Integer.parseInt(applicationConfig.property("hash.saltLength").getString());
        int hashLength = Integer.parseInt(applicationConfig.property("hash.hashLength").getString());
        int parallelism = Integer.parseInt(applicationConfig.property("hash.parallelism").getString());
        int memory = Integer.parseInt(applicationConfig.property("hash.memory").getString());
        int iterations = Integer.parseInt(applicationConfig.property("hash.iterations").getString());

        arg2SpringSecurity = new Argon2PasswordEncoder(saltLength, hashLength, parallelism, memory, iterations);
    }

    public @NotNull String hash(@NotNull String text) {
        return arg2SpringSecurity.encode(text);
    }

    public @NotNull Boolean matches(@NotNull String text, String encoded) {
        return arg2SpringSecurity.matches(text, encoded);
    }
}
