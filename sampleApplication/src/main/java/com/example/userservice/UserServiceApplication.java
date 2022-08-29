package com.example.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableDiscoveryClient  // 유레카 서버에 클라이언트를 등록한다.
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean // BCryptPasswordEncoder을 Bean으로 등록한다.
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean   // @Bean으로 등록하면 컨트롤러, 서비스, 레포지토리에서 주입받아서 사용가능하다.
    @LoadBalanced   //
    public RestTemplate getRestTemplate(){
        return new RestTemplate(); // 반환 시키고자 하는 데이터의 종류를 RestTemplate이라고 명시
    }

}
