package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.repository.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
public class OrderController {

    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;
    OrderProducer orderProducer;
    // KafkaProducer, OrderProducer가 @Service라는 @Bean으로 등록이 되어있기 때문에 해당하는 Bean을 주입한다.

    @Autowired
    public OrderController(Environment env, OrderService orderService,
                           KafkaProducer kafkaProducer, OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s",
                env.getProperty("local.server.port"));
    }

    /* 주문 하기 */
    // http://127.0.0.1:0/order-service/{userId}/orders
    @PostMapping("/{userId}/orders") // 제네릭타입으로 반환할 형태도 명시할 수 있다.
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);


        // RequestUser를 UserDto로 매핑한다.
        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        /* jpa */
//        OrderDto createdOrder = orderService.createOrder(orderDto); // 주문함
//        ResponseOrder responseOrder = mapper.map(createdOrder, ResponseOrder.class); // userDto 를 ResponseUser 형으로 변경한다.

        /* kafka */
        orderDto.setOrderId(UUID.randomUUID().toString());
        orderDto.setTotalPrice(orderDetails.getQty() * orderDetails.getUnitPrice());

        // 카프카에 메시지(주문서비스)를 전달하는 작업을 추가로 넣음
        /* send this order to the kafka */
        kafkaProducer.send("example-catalog-topic", orderDto);  // example-catalog-topic(토픽이름) 으로 orderDto를 전달한다.
        // 당연히 받는 쪽(Consumer)에서도 토픽이름을 지정해야한다.
        orderProducer.send("orders",orderDto);

        ResponseOrder responseOrder = mapper.map(orderDto, ResponseOrder.class); // orderDto를 ResponseOrder 형으로 변경한다.

        // rest api 식으로 반환하자
        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder); // 201번 성공코드 반환
    }

    /* userId가 주문한 목록 보여주기 */
    @GetMapping("/{userId}/orders") // 제네릭타입으로 반환할 형태도 명시할 수 있다.
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId) {
        Iterable<OrderEntity> orderList = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        // 순차적으로 꺼내온 v를 ResponseOrder로 변경한다.
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}


