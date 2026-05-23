package cat.itacademy.s04.t01.userapi.user;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@RestController
public class UserController {
    List<User> users = new ArrayList<>();

    @GetMapping("/users")
    public List<User> getUsers(
            @RequestParam(required = false) String name) {

        if (name != null) {
            return users.stream().filter(user -> user.getName().equalsIgnoreCase(name)).toList();
        }
        return List.copyOf(users);
    }

    @PostMapping("/user")
    public User createUser(@RequestBody CreateUserDto createUserDto) {
        UUID id = UUID.randomUUID();
        User user = new User(createUserDto.name(), createUserDto.email(), id);
        users.add(user);
        return user;
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable String id) {
        return users.stream().filter(user -> user.getUuid().toString().equals(id)).findFirst().orElseThrow(() -> new UserNotFoundException("Could not find the user with id " + id));
    }

    @GetMapping("/users/search/{name}")
    public User getUserByName(@PathVariable String name) {
        return users.stream().filter(user -> user.getName().equalsIgnoreCase(name)).findFirst().orElseThrow(() -> new UserNotFoundException("Could not find the user with name " + name));
    }
}
