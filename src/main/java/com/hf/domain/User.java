package com.hf.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

//为“” 或者为null 都不序列化json
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class User {
    private Integer uid;

    //用户名
    @NotNull
    @Length(min=1,max=20)
    private String username;

    //密码
    @NotNull
    @Length(min=6,max=20)
    private String password;

    //邮箱
    @NotNull
    @Email
    private String email;

    //头像路径
    private String head;
    //注册时间
    private Long registration;
    //是否激活
    private Integer activate;

    private Integer role_id;

    //谁关注了他
    private Integer NumberOfConcerns;

    //他关注了谁
    private Integer ConcernNumber;

    public Integer getNumberOfConcerns() {
        return NumberOfConcerns;
    }

    public void setNumberOfConcerns(Integer numberOfConcerns) {
        NumberOfConcerns = numberOfConcerns;
    }

    public Integer getConcernNumber() {
        return ConcernNumber;
    }

    public void setConcernNumber(Integer concernNumber) {
        ConcernNumber = concernNumber;
    }

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public Long getRegistration() {
        return registration;
    }

    public void setRegistration(Long registration) {
        this.registration = registration;
    }

    public Integer getActivate() {
        return activate;
    }

    public void setActivate(Integer activate) {
        this.activate = activate;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid=" + uid +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", head='" + head + '\'' +
                ", registration=" + registration +
                ", activate=" + activate +
                ", role_id=" + role_id +
                ", NumberOfConcerns=" + NumberOfConcerns +
                ", ConcernNumber=" + ConcernNumber +
                '}';
    }
}
