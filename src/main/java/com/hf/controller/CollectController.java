package com.hf.controller;

import com.hf.domain.Article;
import com.hf.domain.User;
import com.hf.service.CollectService;
import com.hf.vo.ArticleVo;
import com.hf.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@RestController
@RequestMapping("/collect")
@SessionAttributes(value = {"user"}, types = {User.class}) // 把数据存入到session域对象中
public class CollectController {

    @Autowired
    private CollectService collectService;

    /**
     * 收藏文章
     * @param map
     * @param modelMap
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> Collect(@RequestBody Map<String, Integer> map, ModelMap modelMap) {
        Integer aid = map.get("aid");
        User user = (User) modelMap.get("user");
        collectService.Collect(aid, user.getUid());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 是否收藏文章
     * @param aid 文章id
     * @param modelMap
     * @return
     */
    @GetMapping("/{aid}")
    public ResponseEntity<Void> whetherCollect(@PathVariable("aid") Integer aid,ModelMap modelMap) {

        User user = (User) modelMap.get("user");
        collectService.whetherCollect(aid, user.getUid());

        return ResponseEntity.ok().build();
    }

    /**
     * 取消收藏
     * @param aid
     * @param modelMap
     * @return
     */
    @DeleteMapping("/{aid}")
    public ResponseEntity<Void> deleteCollect(@PathVariable("aid") Integer aid,ModelMap modelMap) {

        User user = (User) modelMap.get("user");
        collectService.deleteCollect(aid, user.getUid());

        return ResponseEntity.ok().build();
    }


    /**
     * 当前用户收藏文章
     * @param page
     * @param request
     * @return
     */
    @GetMapping("/all")
    public ResponseEntity<PageResult<ArticleVo>> findAllByUid(@RequestParam(value = "page",defaultValue = "1") Integer page, HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        PageResult<ArticleVo> pageResult = collectService.findAllByUid(page,user.getUid());
        return ResponseEntity.ok(pageResult);
    }

}
