package com.hf.common.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

@Component
@Scope("prototype")
public class Mail {

    //发送邮件对象
    @Autowired
    private JavaMailSenderImpl sender;

    //加密解密
    @Autowired
    private DES des;

    @Value("${hf.desSalt}")
    private String desSalt;

    /**
     * 发送邮件
     * @param consignee 账户
     */
    public void sendMail(String consignee,String title,String content){

        MimeMessage message = sender.createMimeMessage();

        try {
            //发送带附件和内联元素的邮件需要将第二个参数设置为true
            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");

            //接受方邮箱
            helper.setTo(consignee);
            //发送方邮箱，要和配置文件的发送账号一致
            helper.setFrom(new InternetAddress("2879823308@qq.com","很方","UTF-8"));
            //主题
            helper.setSubject(title);
            //内容
            String body = content;
            helper.setText(body,true);
            sender.send(message);
            System.out.println("发送成功...");
        } catch (Exception e) {
            System.out.println("发送失败..."+e.toString());

        }

    }

    /**
     * 加密
     * @param email
     * @return
     */
    public String encryption(String email){
        return des.encryptDES(email,desSalt);
    }

    /**
     * 解密
     * @param ct
     * @return
     */
    public  String decode(String ct){
        return des.decryptDES(ct,desSalt);
    }


}
