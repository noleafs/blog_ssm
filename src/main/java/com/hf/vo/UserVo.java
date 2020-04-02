package com.hf.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotNull;

//为“” 或者为null 都不序列化json
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UserVo {

    private Integer uid;

    private String head;

    //邮箱
    @NotNull
    @Email
    private String emails;

    //密码
    @NotNull
    @Length(min=6,max=20)
    private String passwords;

    //错误次数
    private Integer frequency;

    //标识
    private String code;

    //提交的验证码
    private String vcode;

    private Integer role_id;

    public Integer getRole_id() {
        return role_id;
    }

    public void setRole_id(Integer role_id) {
        this.role_id = role_id;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public String getEmails() {
        return emails;
    }

    public void setEmails(String emails) {
        this.emails = emails;
    }

    public String getPasswords() {
        return passwords;
    }

    public void setPasswords(String passwords) {
        this.passwords = passwords;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "uid=" + uid +
                ", head='" + head + '\'' +
                ", emails='" + emails + '\'' +
                ", passwords='" + passwords + '\'' +
                ", frequency=" + frequency +
                ", code='" + code + '\'' +
                ", vcode='" + vcode + '\'' +
                '}';
    }
}
