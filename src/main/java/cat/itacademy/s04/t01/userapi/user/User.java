package cat.itacademy.s04.t01.userapi.user;

import java.util.UUID;

public class User {
    private UUID uuid;
    private final String name;
    private final String email;

    public User(String name, String email, UUID uuid) {
        this.name = name;
        this.email = email;
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
