package cat.itacademy.s04.t01.userapi.user.service;

import cat.itacademy.s04.t01.userapi.user.dto.CreateUserDto;
import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;

import java.util.List;

public interface UserService {
     List<UserResponse> getAllUsers();
     UserResponse createUser(CreateUserDto createUserDto);
     UserResponse getUserById(String id);
     List<UserResponse> getUserByName(String name);
}
