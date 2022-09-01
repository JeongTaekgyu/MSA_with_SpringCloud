package com.example.catalogservice.messagequeue;

import com.example.catalogservice.repository.CatalogEntity;
import com.example.catalogservice.repository.CatalogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {
    // 실제로 리스너를 이용해서 데이터를 가져와서 db에 있는 값을 update한다.

    CatalogRepository catalogRepository;

    @Autowired
    public KafkaConsumer(CatalogRepository repository){
        this.catalogRepository = repository;
    }

    @KafkaListener(topics = "example-catalog-topic") // 토픽의 이름을 지정
    public void updateQty(String kafkaMessage){
        log.info("Kafka Message: -> " + kafkaMessage);

        // 역직렬화
        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();

        // String형으로 들어오는 kafkaMessage를 JSON타입으로 변경해서 사용할 것이다.
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {});
        } catch (JsonProcessingException ex){
            ex.printStackTrace();
        }

        CatalogEntity entity = catalogRepository.findByProductId((String)map.get("productId"));
        if(entity != null){
            // 원래있던 수량에서 전달된 수량을 빼고 stock에다 저장 시켰다.
            entity.setStock(entity.getStock() - (Integer)map.get("qty"));
            catalogRepository.save(entity);
        }

    }
}
