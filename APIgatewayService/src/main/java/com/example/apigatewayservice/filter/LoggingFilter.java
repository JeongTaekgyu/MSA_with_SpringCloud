package com.example.apigatewayservice.filter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter extends AbstractGatewayFilterFactory<LoggingFilter.Config> {
                                // AbstractGatewayFilterFactory를 상속 받아야 한다.
    public LoggingFilter() {
        super(Config.class);
    }

    //Generate -> implement Methods
    @Override
    public GatewayFilter apply(Config config) {

        GatewayFilter filter = new OrderedGatewayFilter((exchange, chain) ->{
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Logging Filter baseMessage : {}", config.getBaseMessage());

            if(config.isPreLogger()){
                log.info("Logging Pre Filter : request id -> {}", request.getId());
            }
            // Custom Post Filter
            return chain.filter(exchange).then(Mono.fromRunnable(() ->{
                if(config.isPostLogger()){
                    log.info("Logging POST Filter : response code -> {}", response.getStatusCode());
                }
            }));
        }, Ordered.LOWEST_PRECEDENCE);  // Ordered.LOWEST_PRECEDENCE 우선순위를 가장 낮게 잡음, 가장 높게 잡으려면 Ordered.HIGHEST_PRECEDENCE로 하면 된다.
        return filter;
    }

    @Data
    public static class Config{
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;
    }

}