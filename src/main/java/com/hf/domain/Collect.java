package com.hf.domain;

import lombok.Data;

/**
 * 收藏
 */
@Data
public class Collect {
    private Integer cid;
    private Integer user_id;
    private Integer article_id;
    private Long collectTime;
}
