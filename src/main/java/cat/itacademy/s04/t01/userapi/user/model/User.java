package cat.itacademy.s04.t01.userapi.user.model;

import lombok.Getter;

import java.util.UUID;

@Getter
public class User {
    private final UUID uuid;
    private final String name;
    private final String email;

    public User(String name, String email, UUID uuid) {
        this.name = name;
        this.email = email;
        this.uuid = uuid;
    }

}
