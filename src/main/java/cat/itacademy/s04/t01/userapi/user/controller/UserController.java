package cat.itacademy.s04.t01.userapi.user.controller;

import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.service.UserService;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;


@RestController
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/user")
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserDto createUserDto) {
        UserResponse response = userService.createUser(createUserDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @Size(min = 30, max = 40) String id) {
        UserResponse response =  userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/search/{name}")
    public ResponseEntity<List<UserResponse>> getUserByName(@PathVariable String name) {
        List<UserResponse> users =  userService.getUserByName(name);
        return ResponseEntity.ok(users);
    }
}
