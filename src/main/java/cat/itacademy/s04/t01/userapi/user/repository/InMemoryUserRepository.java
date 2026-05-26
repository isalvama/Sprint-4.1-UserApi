package cat.itacademy.s04.t01.userapi.user.repository;

import cat.itacademy.s04.t01.userapi.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InMemoryUserRepository implements UserRepository{

    List<User> users = new ArrayList<>();

    @Override
    public User save(User user) {
        users.add(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return users.stream().findFirst().filter(user -> user.getUuid().equals(id));
    }

    @Override
    public List<User> searchByName(String name) {
        return users.stream().filter(user -> user.getName().equalsIgnoreCase(name)).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}
