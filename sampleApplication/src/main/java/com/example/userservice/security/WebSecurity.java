package com.example.userservice.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;


@Configuration
@EnableWebSecurity
public class WebSecurity extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();    // 모든걸 다 허용
        http.authorizeRequests().antMatchers("/**")
                .hasIpAddress("192.168.45.163")// 해당 ip 허용
                .and()
                .addFilter(getAuthenticationFilter());  // 이 필터를 통과하 데이터에 의해서만 허용한다.(인증)

        http.headers().frameOptions().disable(); // 프레임별로 데이터가 나눠져있는데 그걸 무시한다.
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter = new AuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

}
