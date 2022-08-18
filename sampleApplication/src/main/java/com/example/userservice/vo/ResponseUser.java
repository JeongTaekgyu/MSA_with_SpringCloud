package com.example.userservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
// VO객체를 JSON으로 변환할 때 포함되면 안되는 변수에 @JsonIgnore 어노테이션을 선언하여 JSON에 포함되지 않도록 할 수 있다.
@JsonInclude(JsonInclude.Include.NON_NULL)  // null인 데이터는 제외한다. 즉, null 인 데이터는 ResponseUser에 포함 시키지 않는다.
public class ResponseUser {

    private String email;
    private String name;
    private String userId;

    private List<ResponseOrder> orders;
}
