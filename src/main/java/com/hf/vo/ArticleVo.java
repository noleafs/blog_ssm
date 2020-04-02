package com.hf.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ArticleVo {

    //文章id
    private Integer aid;

    //浏览量
    private Integer pageview;

    //作者名
    private String username;

    //标题
    private String title;

    //描述
    private String describe;

//    //通过该标识找图片
//    private String tag;

    //发布时间 为时间戳
    private Long releasetime;

    //文章图片
    private String imgPath;
}
