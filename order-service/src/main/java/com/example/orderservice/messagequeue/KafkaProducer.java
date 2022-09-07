package com.example.orderservice.messagequeue;

import com.example.orderservice.dto.OrderDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducer {

    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public OrderDto send(String topic, OrderDto orderDto) {
        // 주문 서비스를 json 포맷으로 변경
        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            // String으로 전달하는데 이것을 json 형태를 가질 수 있도록 mapper 기능을 사용함. 즉, String인데 JSON 형태처럼 생김
            jsonInString = mapper.writeValueAsString(orderDto);
        } catch (JsonProcessingException ex){
            ex.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonInString);
        // kafka에서 메시지가 잘 보내졌다고 로그 찍음
        log.info("kafka Producer sent data from the Order microservice: " + orderDto);

        return orderDto;
    }
}
