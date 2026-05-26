package cat.itacademy.s04.t01.userapi.user.service;

import cat.itacademy.s04.t01.userapi.user.exception.UserAlreadyExistsException;
import cat.itacademy.s04.t01.userapi.user.exception.UserNotFoundException;
import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.model.User;
import cat.itacademy.s04.t01.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse createUser(CreateUserDto createUserDto) {
        boolean exists = userRepository.existsByEmail(createUserDto.email());
        if (exists) {
            throw new UserAlreadyExistsException(String.format(createUserDto.email()));
        }
        UUID id = UUID.randomUUID();
        User newUser = new User(createUserDto.name(), createUserDto.email(), id);
        userRepository.save(newUser);
        return userMapper.toResponse(newUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()){
            return List.of();
        }
        return users.stream().map(userMapper::toResponse).toList();
    }

    @Override
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new UserNotFoundException("id", id));
        return userMapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getUserByName(String name) {
        List<User> users = userRepository.searchByName(name);
        if (users.isEmpty()){
            throw new UserNotFoundException("name", name);
        }
        return users.stream().map(userMapper::toResponse).toList();
    }
}
