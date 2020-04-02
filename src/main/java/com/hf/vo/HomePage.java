package com.hf.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HomePage {
    //用户名
    private String username;
    //用户邮箱
    private String email;
    //用户头像
    private String head;
    //用户注册时间
    private Long registration;
    //粉丝总数
    private Integer fansCount;
    //关注总数
    private Integer payCount;
    //发表文章总数
    private Integer articleCount;
    //收藏文章总数
    private Integer collectCount;
}
