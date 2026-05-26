package cat.itacademy.s04.t01.userapi.user.service;

import cat.itacademy.s04.t01.userapi.user.dto.UserResponse;
import cat.itacademy.s04.t01.userapi.user.model.User;

public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getName(),
                user.getEmail(),
                user.getUuid().toString()
        );
    }
}