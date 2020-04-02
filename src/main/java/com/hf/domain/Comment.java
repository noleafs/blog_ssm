package com.hf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论回复实体类
 */
@Data
public class Comment {
    //评论id
    private Integer cid;
    //帖子id
    private Integer article_id;

    //评论者id
    private Integer user_id;

    //评论者信息
    private User user;

    //评论内容
    private String commentContent;

    //评论时间
    private Long commentTime;

    //评论还是回复
//    @JsonIgnore
    private Integer cid_id;

    //根评论id
    private Integer rootid;

    //回复
    private List<Comment> replyList = new ArrayList<>();

 }
