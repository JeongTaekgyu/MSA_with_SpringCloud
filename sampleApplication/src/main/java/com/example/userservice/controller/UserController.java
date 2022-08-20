package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.repository.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.Greeting;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/")
public class UserController {

    private Environment env;
    private UserService userService;

    @Autowired  // 권장하지 않는 방법 그냥 간단한 예제니까 한다.
    private Greeting greeting;

    @Autowired
    public UserController(Environment env, UserService userService) {
        this.env = env;
        this.userService = userService;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in User Service"
                + ", port(local.server.port)="+ env.getProperty("local.server.port")
                + ", port(server.port)="+ env.getProperty("server.port")
                + ", token secret="+ env.getProperty("local.secret")
                + ", token expiration time="+ env.getProperty("token.expiration_time"));

    }

    @GetMapping("/welcome")
    public String welcome() {   
        // yml 파일에 있는 내용 출력 2가지 방법
        //return env.getProperty("greeting.message");
        return greeting.getMessage();
    }

    @PostMapping("/users") // 제네릭타입으로 반환할 형태도 명시할 수 있다.
    public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser user) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        // RequestUser를 UserDto로 매핑한다.
        UserDto userDto = mapper.map(user, UserDto.class);
        userService.createUser(userDto);

        // userDto 를 ResponseUser 형으로 변경한다.
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        // rest api 식으로 반환하자
        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser); // 201번 성공코드 반환
    }

    @GetMapping("/users")
    public ResponseEntity<List<ResponseUser>> getUsers() {
        Iterable<UserEntity> userList = userService.getUserByAll();

        List<ResponseUser> result = new ArrayList<>();
        userList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseUser.class));
        }); // v를 ResponseUser 클래스로 변경

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<ResponseUser> getUser(@PathVariable("userId") String userId) {
        UserDto userDto = userService.getUserByUserId(userId);

        ResponseUser returnValue = new ModelMapper().map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.OK).body(returnValue);
    }
}
