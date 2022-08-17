package com.example.userservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Greeting {

    @Value("${greeting.message}")    // yml 파일에 있는 값을 가져오기 위한 방법
    private String message;

}