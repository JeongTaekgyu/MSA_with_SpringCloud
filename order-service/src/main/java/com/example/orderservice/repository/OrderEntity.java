package com.example.orderservice.repository;

import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@Entity
@Table(name="orders")
public class OrderEntity implements Serializable {
    // 가지고 있는 객체를 네트워크로 전송하거나 데이터베이스에 보관하기 위해서 사용한다.
    // 마샬링, 언마샬링 작업하기 위해서 직력화를 사용한다.

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true) // 이것 때문에 주문하면 유니크 에러나는듯
    private String productId;

    @Column(nullable = false)
    private Integer qty;

    @Column(nullable = false)
    private Integer unitPrice;

    @Column(nullable = false)
    private Integer totalPrice;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false, updatable = false, insertable = false)
    @ColumnDefault(value = "CURRENT_TIMESTAMP") // 현재 날짜를 가져오기 위한 방법
    private Date createdAt;
}
