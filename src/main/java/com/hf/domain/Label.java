package com.hf.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Label {

    private Integer id;

    @JsonProperty("text") //序列化时 为这个键名
    private String classname;

    private long labelTime;

}
