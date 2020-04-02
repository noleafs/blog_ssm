package com.hf.controller;

import com.alibaba.druid.support.json.JSONUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hf.common.utils.CheckCode;
import com.hf.common.utils.CookieUtils;
import com.hf.domain.User;
import com.hf.service.UserService;
import com.hf.vo.HomePage;
import com.hf.vo.PageResult;
import com.hf.vo.UserVo;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.validation.Valid;
import javax.xml.transform.OutputKeys;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

//@CrossOrigin(origins = {"http://127.0.0.1:8848","http://localhost:8848"}) // 允许所有ip跨域
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 注册用户
     * @param user
     * @return
     */
    @PostMapping("/sign")
    public ResponseEntity<Void> addUser(@Valid User user){
        userService.addUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/reactivation")
    public ResponseEntity<Void> reactivation(@RequestParam(value = "email",required = true) String email){
        userService.reactivation(email);
        return ResponseEntity.ok().build();
    }

    /**
     * 登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@Valid UserVo user, HttpServletRequest request,HttpServletResponse resp){
        UserVo user1 = userService.login(user);
        User user2 = new User();
        user2.setUid(user1.getUid());
        user2.setRole_id(user1.getRole_id());

        //存储登录成功标识
        request.getSession().setAttribute("user",user2);

        System.out.println("用户是：  "+request.getSession().getAttribute("user"));
        return ResponseEntity.status(HttpStatus.OK).build();
    }




    /**
     * 激活用户
     * @param t
     * @return
     */
    @GetMapping("/activate")
    public ResponseEntity<Void> activate(String t,HttpServletResponse response) throws IOException {
        userService.activate(t);

        response.sendRedirect("/index.html");
        return ResponseEntity.status(HttpStatus.OK).build();
    }


    /**
     * 生成验证码图片及返回
     * @param code 代表一个标识
     * @param resp
     * @throws Exception
     */
    @GetMapping("/checkcode")
    @Scope("prototype")
    public void checkCode(String code,HttpServletResponse resp) throws Exception {
        // 返回生成的验证码
        String s = CheckCode.getCode(resp);
       // 将验证码保存，等会以比较
        userService.checkCode(s,code);
    }

    /**
     * 判断用户名 或者 邮箱是否存在
     * @param
     * @return
     */
    @GetMapping("/exists")
    public ResponseEntity<Boolean> exists(String username,String email){
        return ResponseEntity.ok(userService.exists(username,email));
    }

    /**
     * 是否登录
     * @param request
     * @return
     */
    @GetMapping("/thelogin")
    public ResponseEntity<UserVo> theLogin(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");

        // uid等于null 代表没有登录
        if (user == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ResponseEntity.ok(userService.theLogin(user.getUid()));
    }



    /**
     * 退出
     * @param request
     * @return
     */
    @GetMapping("/exit")
    public ResponseEntity<Void> exit(HttpServletRequest request){
        try {
            request.getSession().setAttribute("user",null);
        }catch (Exception e){

        }
        return ResponseEntity.ok().build();
    }


    /**
     * 个人主页
     */
    @GetMapping("/homepage")
    public ResponseEntity<HomePage> homePage(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        HomePage homePage = userService.homePage(user.getUid());
        return ResponseEntity.ok(homePage);
    }

    /**
     * 作者信息
     * @param id
     * @return
     */
    @GetMapping("/author")
    public ResponseEntity<HomePage> author(Integer id){
        return ResponseEntity.ok(userService.author(id));
    }

    /**
     * 资料修改
     * @param file
     * @param username
     * @param email
     * @param code
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> updateUser(@RequestParam(value = "file",required = false) MultipartFile file,
                                           String username,
                                           String email,
                                           String code,
                                           String rawpassword,
                                           String password,
                                           HttpServletRequest request
    ){
        userService.updateUser(file,username,email,code,request,rawpassword,password);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sendmail")
    public ResponseEntity<Void> sendMail(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        userService.sendMail(user.getUid());
        return ResponseEntity.ok().build();
    }

    /**
     * 找回密码
     * @param email
     * @return
     */
    @PostMapping("/forget")
    public ResponseEntity<Void> forget(String email){
        userService.forget(email);
        return ResponseEntity.ok().build();


    }

    /**
     * 校验
     * @param t
     * @return
     */
    @GetMapping("/forget")
    public ResponseEntity<String> gForget(String t){
        return ResponseEntity.ok(userService.gForget(t));
    }


    /**
     * 修改密码
     * @param user
     * @return
     */
    @PutMapping("/forget")
    public ResponseEntity<Void> pForget(User user){

        userService.pForget(user);

        return ResponseEntity.ok().build();
    }

    /**
     * 账户错误次数
     * @param email
     * @return
     */
    @GetMapping("/failfrequency")
    public ResponseEntity<Integer> fail(String email){
        return ResponseEntity.ok(userService.fail(email));
    }


    //----------------------------------------------------------------------------------------------------------



    /**
     * 管理员是否登录
     * @param request
     * @return
     */
    @GetMapping("/admin/thelogin")
    public ResponseEntity<User> adminTheLogin(HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");

        return ResponseEntity.ok(userService.adminTheLogin(user.getUid()));
    }



    /**
     * 查询所有用户
     * @return
     */
    @GetMapping("/admin/all")
    public ResponseEntity<PageResult<User>> adminUserAll(@RequestParam(value = "page",defaultValue = "1") Integer page, String q){

        return ResponseEntity.ok(userService.adminUserAll(page,q));
    }


    /**
     * 删除用户
     * @param uid
     * @return
     */
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> adminUserDelete(@PathVariable(name = "id",required = true) Integer uid){
        userService.adminUserDelete(uid);
        return ResponseEntity.ok().build();
    }


    /**
     * 修改用户
     * @param uid
     * @param user
     * @return
     */
    @PutMapping("/admin/{id}")
    public ResponseEntity<Void> adminUserPut(@PathVariable(name = "id",required = true) Integer uid,@RequestBody User user){

        userService.adminUserPut(user);
        return ResponseEntity.ok().build();
    }


}
