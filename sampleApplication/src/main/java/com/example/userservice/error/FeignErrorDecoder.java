package com.example.userservice.error;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component // 개발자가 직접 작성한 Class를 Bean으로 등록하기 위한 Annotation
public class FeignErrorDecoder implements ErrorDecoder {

    Environment env;

    @Autowired
    public FeignErrorDecoder(Environment env){
        this.env = env;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()){
            case 400:
                break;
            case 404:
                if(methodKey.contains("getOrders")){
                    return new ResponseStatusException(HttpStatus.valueOf(response.status()),
                            env.getProperty("order_service.exception.orders_is_empty"));
                }
                break;
            default:
                return new Exception(response.reason());
        }

        return null;
    }
}
