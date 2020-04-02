package com.hf.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Role {
    private Integer rid;
    private String role_name;
    private Integer size;
    private Long roleTime;
}
