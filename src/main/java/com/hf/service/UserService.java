package com.hf.service;

import com.hf.domain.User;
import com.hf.vo.HomePage;
import com.hf.vo.PageResult;
import com.hf.vo.UserVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface UserService {

    /**
     * 添加一个用户
     * @param user
     */
    void addUser(User user);

    /**
     * 登录
     * @param user
     */
    UserVo login(UserVo user);

    void checkCode(String s,String code);

    void activate(String t);

    Boolean exists(String username, String email);

    UserVo theLogin(Integer uid);

    HomePage homePage(Integer uid);

    HomePage author(Integer id);

    void sendMail(Integer uid);

    void updateUser(MultipartFile file, String username, String email, String code, HttpServletRequest request,String rawPassword,String password);

    void forget(String email);

    String gForget(String t);

    void pForget(User user);

    Integer fail(String email);

    PageResult<User> adminUserAll(Integer page, String q);

    void adminUserDelete(Integer uid);

    User adminTheLogin(Integer uid);

    void adminUserPut(User user);

    void reactivation(String email);
}
