package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;

public interface UserService {
    UserDto createUser(UserDto userDto);
    
    UserDto getUserByUserId(String userId);
    Iterable<UserEntity> getUserByAll();    // 엔티티에 있는거 바로 가져오는거 좋지 않지만 그냥 간단한 예제니 쓰자

}
