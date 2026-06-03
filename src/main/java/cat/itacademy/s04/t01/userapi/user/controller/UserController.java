package cat.itacademy.s04.t01.userapi.user.controller;

import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import java.util.List;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/users")

public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        UserResponse response = userService.createUser(createUserDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(value = "name", required = false) String name) {
        if (name != null && !name.isBlank()){
            List<UserResponse> filteredUsers =  userService.getUserByName(name);
            return ResponseEntity.ok(filteredUsers);
        }

        List<UserResponse> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @UUID(message = "The ID must be a valid UUID") String id) {
        UserResponse response =  userService.getUserById(id);
        return ResponseEntity.ok(response);
    }
}
