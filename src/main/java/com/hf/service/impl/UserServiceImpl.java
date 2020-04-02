package com.hf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.hf.common.exception.HfException;
import com.hf.dao.UserAdminDao;
import com.hf.dao.UserDao;
import com.hf.domain.User;
import com.hf.service.UserService;
import com.hf.common.utils.Mail;
import com.hf.vo.HomePage;
import com.hf.vo.PageResult;
import com.hf.vo.UserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;


import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final static String prefix = "sign_";
    private final static String error = "error_";
    private final static String updatePrefix = "update_";
    private final static String forgetPrefix = "forget_";

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserAdminDao userAdminDao;

    @Value("${hf.default.avatar}")
    private String headPath;

    //盐
    @Value("${hf.salt}")
    private String salt;

    @Value("${hf.period}")
    private Long period;

    @Value("${hf.url}")
    private String url;

    @Autowired
    private Mail mail;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private FastFileStorageClient storageClient;

    @Override
    public void addUser(User user) {
        //加密
        String password = user.getPassword() + salt;
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        user.setHead(headPath);
        user.setRegistration(System.currentTimeMillis());
        user.setActivate(0);

        try {
            int i = userDao.saveUser(user);
            if (i != 1) {
                throw new HfException(500, "注册失败");
            }
        } catch (Exception e) {
            log.info("[用户注册] 用户注册出现异常",e);
            throw new HfException(500, "注册失败");
        }

        // 存储redis,并设置有效期
        redisTemplate.opsForValue().set(prefix + user.getEmail(), user.getUsername(), period, TimeUnit.MINUTES);

        // 发送激活邮件
        new Thread(() -> {
            mail.sendMail(user.getEmail(),"这是一封激活邮件","<html><body><a href=\"http://121.40.240.223/user/activate?t="+mail.encryption(user.getEmail())+"\">点击此，来激活你的账号</a></body></html>");
        }).start();


    }

    // 存储账户错误的次数
    private int add(UserVo user) {
        String slag = error + user.getEmails();
        ValueOperations<String, String> redis = redisTemplate.opsForValue();
        String s = redis.get(slag);
        // 判断是否存在，不存在则代表是第一次错误
        if (StringUtils.isEmpty(s)) {
            redis.set(slag, "1", 10L, TimeUnit.MINUTES);
            return 1;
        }
        // 存在则将次数+1
        int i = Integer.parseInt(s);
        redis.set(slag, String.valueOf(++i), 10L, TimeUnit.MINUTES);
        return i;

    }

    @Override
    public UserVo login(UserVo user) {
//        System.out.println(user);
        //比较验证码
        ValueOperations<String, String> ssvo = redisTemplate.opsForValue();
        String authCode = ssvo.get(error + user.getEmails());
        if (authCode != null && Integer.valueOf(authCode) >= 5) {
            // 代表错误次数达到了5次，此时就需要比较验证码了
            // 用户提交的验证码
            String vcode = user.getVcode();
            // 标识，通过该标识进行判断
            String code = user.getCode();
            String s = ssvo.get(code);
            if (!s.equalsIgnoreCase(vcode)) {
                System.out.println("验证码错误");
                throw new HfException(400, "{\"msg\":\"验证码错误\",\"error\":\"" + Integer.valueOf(authCode) + "\"}");
            }
            // 验证码正确 删除redis中的验证码
            redisTemplate.delete(code);

        }

        //查询出账户的用户信息
        User login = userDao.queryByEmail(user.getEmails());

        if (login == null) {
            int i = add(user);
            throw new HfException(400, "{\"msg\":\"用户名或密码错误\",\"error\":\"" + i + "\"}");
        }


        // 判断密码是否正确
        String password = user.getPasswords() + salt;
        String s = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!StringUtils.equals(login.getPassword(), s)) {
            int i = add(user);
            throw new HfException(400, "{\"msg\":\"用户名或密码错误\",\"error\":\"" + i + "\"}");
        }

        // 判断是否激活
        if (login.getActivate() == 0) {
            throw new HfException(400, "{\"msg\":\"你的账户未激活\"}");
        }

        // 删除错误次数
        redisTemplate.delete(error + user.getEmails());

        user = new UserVo();
        user.setUid(login.getUid());
        user.setRole_id(login.getRole_id());

        return user;
    }

    @Override
    public void checkCode(String s, String code) {
        redisTemplate.opsForValue().set(code, s, 2L, TimeUnit.MINUTES);
    }

    @Override
    public void activate(String t) {
        String email;
        try {
            // 解密
            email = mail.decode(t);
            if (email == null) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new HfException(400, "激活异常..");
        }
        // 从redis中取这个账户 查看是否过期
        String username = redisTemplate.opsForValue().get(prefix + email);
        if (username == null) {
            throw new HfException(410, "链接已失效请重新激活..");
        }

        //激活账户
        userDao.updateByEmail(email);

        //激活成功后 删除
        redisTemplate.delete(prefix + email);

    }


    @Override
    public Boolean exists(String username, String email) {
        int i;
        if (StringUtils.isNotEmpty(username)) {
            i = userDao.queryCountByUsername(username);
        } else {
            i = userDao.queryCountByEmail(email);
        }

        return i == 1 ? false : true;

    }

    @Override
    public UserVo theLogin(Integer uid) {
        // 获得当前登录用户的相关信息
        User user = userDao.queryByPrimary(uid);
        UserVo userVo = new UserVo();
        userVo.setUid(user.getUid());
        userVo.setHead(url + user.getHead());

        return userVo;
    }

    @Override
    public HomePage homePage(Integer uid) {
        HomePage homePage = userDao.queryHomePage(uid);
        if (homePage == null) {
            throw new HfException(400,"查询异常");
        }

        return homePage;
    }

    @Override
    public HomePage author(Integer id) {
        HomePage homePage = userDao.queryHomePage(id);
        // 清空不必要的数据
        homePage.setEmail(null);
        homePage.setRegistration(null);
        homePage.setCollectCount(null);
        homePage.setHead(url + homePage.getHead());
        if (homePage == null) {
            throw new HfException(400,"查询异常");
        }
        return homePage;
    }

    @Override
    public void sendMail(Integer uid) {
        String code = String.format("%04d", new Random().nextInt(9999));

        User user = userDao.userByUid(uid);

        redisTemplate.opsForValue().set(updatePrefix + user.getEmail(),code,period, TimeUnit.MINUTES);

        // 发送激活邮件
        new Thread(() -> {
            mail.sendMail(user.getEmail(),"修改邮箱邮件","<html><body><p>您的验证码是：</p><h2>"+code+"</h2></body></html>");
        }).start();


    }

    @Override
    public void updateUser(MultipartFile file, String username, String email, String code, HttpServletRequest request,String rawPassword,String password) {
        User user = (User) request.getSession().getAttribute("user");
        user = userDao.userByUid(user.getUid());

        //不为空代表要修改头像
        if (file != null){
            try {
                String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");

                StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
                //上传的新头像的路径
                String fullPath = storePath.getFullPath();

                try {
                    //删除旧头像
                    storageClient.deleteFile(user.getHead());
                }catch (Exception e){

                }

                user.setHead(fullPath);

            } catch (Exception e) {
                e.printStackTrace();
                throw new HfException(400,"头像上传失败，请稍后再试");
            }

        }

        //username不为空 代表要修改用户名
        if (StringUtils.isNotEmpty(username)){
            user.setUsername(username);
        }

        //email不为空代表要修改email，此时需要对验证码进行校验
        if (StringUtils.isNotEmpty(email)){

            String s = redisTemplate.opsForValue().get(updatePrefix + user.getEmail());
           if (s == null){
               throw new HfException(400,"验证码无效");
           }
           if (!s.equals(code)){
               throw new HfException(400,"验证码错误");
           }

           user.setEmail(email);
        }
        //密码不为空代表要修改密码
        if (StringUtils.isNotEmpty(password)){
             String p = rawPassword + salt;
             //首先对比原始密码是否正确
            if (user.getPassword().equals(DigestUtils.md5DigestAsHex(p.getBytes()))){
                //密码正确，加密新密码
                p = password + salt;
                user.setPassword(DigestUtils.md5DigestAsHex(p.getBytes()));
            }else{
                throw new HfException(400,"原始密码错误");
            }
        }


        Integer i = userDao.userUpdate(user);
        if (i != 1){
            throw new HfException(400,"修改出错");
        }

    }

    @Override
    public void forget(String email) {

        redisTemplate.opsForValue().set(forgetPrefix + email,email,60, TimeUnit.MINUTES);

        new Thread(() -> {
            mail.sendMail(email,"密码找回","<html><body><a href=\"http://121.40.240.223/changepassword.html?t="+mail.encryption(email)+"\">点击此，来找回你的密码</a></body></html>");
        }).start();

    }

    @Override
    public String gForget(String t) {
        String email;

        try {
            email = mail.decode(t);

            String s = redisTemplate.opsForValue().get(forgetPrefix + email);
            if (StringUtils.isEmpty(s)){
                throw new HfException(400,"已过期");
            }
        }catch (Exception e){
            throw new HfException(400,"找回密码异常");
        }

        return email;
    }


    @Override
    public void pForget(User user) {

        String s = redisTemplate.opsForValue().get(forgetPrefix + user.getEmail());
        if (StringUtils.isEmpty(s)){
            throw new HfException(400,"有效期已过");
        }
        redisTemplate.delete(forgetPrefix + user.getEmail());
        System.out.println(user);

        String password = user.getPassword() + salt;
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        System.out.println(user);
        Integer i = userDao.retrievePass(user);
        if (i != 1){
            throw new HfException(400,"找回密码出现异常");
        }

    }

    @Override
    public Integer fail(String email) {
        String slag = error + email;
        String s = redisTemplate.opsForValue().get(slag);
        if (StringUtils.isEmpty(s)){
            return 0;
        }
        return Integer.valueOf(s);
    }

    //---------------------------------------------------------------------------------------------------

    @Override
    public PageResult<User> adminUserAll(Integer page, String q) {
        PageHelper.startPage(page,10);

        List<User> users = userAdminDao.finAll(q);

        PageInfo<User> info = new PageInfo<>(users);
        return new PageResult<>(info.getTotal(), (long) info.getPages(),users);
    }

    @Override
    public void adminUserDelete(Integer uid) {
        User user = userDao.userByUid(uid);
        if (user.getRole_id() != 1){
            userAdminDao.adminUserDelete(uid);
        }else{
            throw new HfException(403,"站长无法删除");
        }

    }

    @Override
    public User adminTheLogin(Integer uid) {
        User user = userAdminDao.queryByPrimary(uid);
        user.setHead(url + user.getHead());
        return user;
    }


    @Override
    public void adminUserPut(User user) {

        if (StringUtils.isEmpty(user.getPassword())){
            user.setPassword(null);
        }else{
            //不是空则代表要修改密码 进行加密
            String password = user.getPassword() + salt;
            user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
        }
        System.out.println(user + "---------------------");
        Integer i = userAdminDao.adminUserPut(user);
        if (i != 1){
            throw new HfException(400,"修改失败");
        }
    }

    @Override
    public void reactivation(String email) {

        // 存储redis,并设置有效期
        redisTemplate.opsForValue().set(prefix + email, email, period, TimeUnit.MINUTES);

        // 发送激活邮件
        new Thread(() -> {
            mail.sendMail(email,"这是一封激活邮件","<html><body><a href=\"http://121.40.240.223/user/activate?t="+mail.encryption(email)+"\">点击此，来激活你的账号</a></body></html>");
        }).start();

    }


}
