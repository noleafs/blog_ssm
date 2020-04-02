package com.hf.controller;

import com.hf.domain.User;
import com.hf.service.AttentionService;
import com.hf.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/attention")
public class AttentionController {

    @Autowired
    private AttentionService attentionService;

    /**
     * 关注用户
     * @param map
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> attention(@RequestBody Map<String, Integer> map, HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");
        //登录用户id
        Integer uid = user.getUid();
        //作者id
        Integer aUid = map.get("auid");

        if (uid == aUid){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }

        attentionService.attention(uid,aUid);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 是否关注用户
     * @param aUid 文章作者id
     * @param request
     * @return
     */
    @GetMapping("/{aUid}")
    public ResponseEntity<Void> whetherAttention(@PathVariable("aUid") Integer aUid,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        attentionService.whetherAttention(user.getUid(),aUid);

        return ResponseEntity.ok().build();
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{aUid}")
    public ResponseEntity<Void> deleteAttention(@PathVariable("aUid") Integer aUid,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute("user");

        attentionService.deleteAttention(user.getUid(),aUid);

        return ResponseEntity.ok().build();
    }


    /**
     * 查询所有粉丝
     * @param page
     * @param request
     * @return
     */
    @GetMapping("/fans")
    public ResponseEntity<PageResult<User>> Fans(@RequestParam(value = "page",defaultValue = "1") Integer page,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        PageResult<User> userPageResult = attentionService.Fans(page,user.getUid());
        return ResponseEntity.ok(userPageResult);
    }

    /**
     * 查询所有关注
     * @param page
     * @param request
     * @return
     */
    @GetMapping("/concern")
    public ResponseEntity<PageResult<User>> Concern(@RequestParam(value = "page",defaultValue = "1") Integer page,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        PageResult<User> userPageResult = attentionService.Concern(page,user.getUid());
        return ResponseEntity.ok(userPageResult);
    }

}
