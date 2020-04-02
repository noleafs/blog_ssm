package com.hf.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Article {

    private Integer aid;

    private Integer user_id;

    private Long pageview;

    @NotNull
    private String title;

    private String describe;

    @NotNull
    private String content;

    private Integer lable_id;

    private String tag;


    private Long releasetime;

    public Integer getAid() {
        return aid;
    }

    public void setAid(Integer aid) {
        this.aid = aid;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Long getPageview() {
        return pageview;
    }

    public void setPageview(Long pageview) {
        this.pageview = pageview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLable_id() {
        return lable_id;
    }

    public void setLable_id(Integer lable_id) {
        this.lable_id = lable_id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getReleasetime() {
        return releasetime;
    }

    public void setReleasetime(Long releasetime) {
        this.releasetime = releasetime;
    }

    @Override
    public String toString() {
        return "Article{" +
                "aid=" + aid +
                ", user_id=" + user_id +
                ", pageView=" + pageview +
                ", title='" + title + '\'' +
                ", describe='" + describe + '\'' +
                ", lable_id=" + lable_id +
                ", tag='" + tag + '\'' +
                ", releasetime=" + releasetime +
                '}';
    }
}
