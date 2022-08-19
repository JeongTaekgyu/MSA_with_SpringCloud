package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.apache.catalina.User;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder){
        this.env = env;
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {  // 권한
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();    // 모든걸 다 허용
        http.authorizeRequests().antMatchers("/**")
//                .hasIpAddress("192.168.45.163")// 해당 ip 허용
                .permitAll()
                .and()
                .addFilter(getAuthenticationFilter());  // 이 필터를 통과하 데이터에 의해서만 허용한다.

        http.headers().frameOptions().disable(); // 프레임별로 데이터가 나눠져있는데 그걸 무시한다.
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter =
                new AuthenticationFilter(authenticationManager(), userService, env);
//        authenticationFilter.setAuthenticationManager(authenticationManager()); // 생성자를 통해서 만들어서 필요없음

        return authenticationFilter;
    }

    // select pwd from users where email = ?
    // db_pwd(encrypted) == input_pwd(encrypted)
    @Override   // 인증 (인증이 되야지만 권한 부여가 가능)
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
