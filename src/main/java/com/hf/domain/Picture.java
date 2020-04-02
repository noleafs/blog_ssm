package com.hf.domain;

import lombok.Data;

@Data
public class Picture {
    private Integer pid;
    private String article_tag;
    private String path;
    private Long picturetime;
    private Integer orphan = 0;

    public Picture() {

    }

    public Picture(Integer pid, String article_tag, String path, Long pictureTime) {
        this.pid = pid;
        this.article_tag = article_tag;
        this.path = path;
        this.picturetime = pictureTime;
    }

//    public Integer getOrphan() {
//        return orphan;
//    }
//
//    public void setOrphan(Integer orphan) {
//        this.orphan = orphan;
//    }
//
//    public Integer getPid() {
//        return pid;
//    }
//
//    public void setPid(Integer pid) {
//        this.pid = pid;
//    }
//
//    public String getArticle_tag() {
//        return article_tag;
//    }
//
//    public void setArticle_tag(String article_tag) {
//        this.article_tag = article_tag;
//    }
//
//    public String getPath() {
//        return path;
//    }
//
//    public void setPath(String path) {
//        this.path = path;
//    }
//
//    public Long getPicturetime() {
//        return picturetime;
//    }
//
//    public void setPicturetime(Long picturetime) {
//        this.picturetime = picturetime;
//    }
}
