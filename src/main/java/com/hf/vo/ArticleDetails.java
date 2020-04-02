package com.hf.vo;

import com.hf.domain.Comment;
import com.hf.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class ArticleDetails {
    //文章id
    private Integer aid;
    //文章标题
    private String title;
    //浏览量
    private Long pageView;
    //文章内容
    private String content;
    //文章分类
    private String labelString;
    //发布时间
    private Long releaseTime;
    //作者信息
    private User user;
    //评论
    private List<Comment> commentList;
}
