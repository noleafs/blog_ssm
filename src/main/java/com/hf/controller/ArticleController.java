package com.hf.controller;

import com.hf.common.exception.HfException;
import com.hf.domain.Article;
import com.hf.domain.User;
import com.hf.service.ArticleService;
import com.hf.vo.*;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 图片上传
     * @param file
     * @return
     */
    @PostMapping("/article/upload")
    public ResponseEntity<ImageVo> imageUpload(@RequestParam(value = "editormd-image-file",required = true) MultipartFile file,String tag){
       return ResponseEntity.status(HttpStatus.CREATED).body(articleService.imageUpload(file,tag));
    }

    /**
     * 保存文章
     * @return
     */
    @PostMapping("/article")
    public ResponseEntity<Void> article(@Valid Article a, HttpServletRequest request){
        User user = (User)request.getSession().getAttribute("user");

        a.setUser_id(user.getUid());
        articleService.article(a);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    /**
     * 查询详情
     * @param aid
     * @return
     */
    @GetMapping("/article/{aid}")
    public ResponseEntity<ArticleDetails> queryById(@PathVariable(name = "aid",required = true) Integer aid){
        return ResponseEntity.ok(articleService.queryById(aid));
    }

    /**
     * 修改
     * @param aid
     * @return
     */
    @PutMapping("/article/{aid}")
    public ResponseEntity<Void> updateArticle(@PathVariable(name = "aid",required = true) Integer aid,Article article,HttpServletRequest request){

        User user = (User) request.getSession().getAttribute("user");

        article.setUser_id(user.getUid());
        System.out.println(article);
        articleService.updateArticle(aid,article);

        return ResponseEntity.ok().build();
    }


    /**
     * 分页查询
     * @param page 页码
     * @param uid 用户id
     * @param lid 分类id
     * @param q 搜索内容
     * @param sort 如何显示文章
     * @return
     */
    @GetMapping("/article/all")
    public ResponseEntity<PageResult<ArticleVo>> findAll(@RequestParam(value = "page",defaultValue = "1") String page,
                                                         @RequestParam(value = "uid",required = false) Integer uid,
                                                         @RequestParam(value = "lid",required = false) Integer lid,
                                                         @RequestParam(value = "q",required = false) String q,
                                                         @RequestParam(value = "sort",required = false,defaultValue = "1") Integer sort,
                                                         @RequestParam(value = "size",required = false,defaultValue = "10") Integer size,
                                                         HttpServletRequest request
    ){

        Integer userId = 0;
         if (sort == 3){
             User user = (User) request.getSession().getAttribute("user");
             userId = user.getUid();
         }
        return ResponseEntity.ok(articleService.findAll(page,uid,lid,q,sort,userId,size));
    }

    /**
     * 删除文章
     */
    @DeleteMapping("/article/{aid}")
    public ResponseEntity<Void> deleteById(@PathVariable(name = "aid",required = true) Integer aid,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");

        if (user == null){
            throw new HfException(401,"未登录");
        }

        articleService.deleteById(user.getUid(),aid);
        return ResponseEntity.ok().build();
    }

    /**
     * 回显内容
     * @param aid
     * @param request
     * @return
     */
    @GetMapping("/article/revamp/{aid}")
    public ResponseEntity<Article> revamp(@PathVariable(value = "aid",required = true)Integer aid,HttpServletRequest request){
        User user = (User) request.getSession().getAttribute("user");
        return ResponseEntity.ok(articleService.revamp(aid,user.getUid()));
    }


    /**
     * 热门文章
     * @return
     */
    @GetMapping("/article/hot")
    public ResponseEntity<List<ArticleVo>> hot(){
        return ResponseEntity.ok(articleService.hot());
    }



    //----------------------------------------------------------------------------------------------

    /**
     * 查询所有文章
     * @return
     */
    @GetMapping("/article/admin/all")
    public ResponseEntity<PageResult<ArticleManager>> adminArticleAll(@RequestParam(value = "page",defaultValue = "1") Integer page, String q){

        return ResponseEntity.ok(articleService.adminArticleAll(page,q));
    }

    @DeleteMapping("/article/admin/{id}")
    public ResponseEntity<Void> adminArticleDelete(@PathVariable(name = "id",required = true) Integer aid){
        articleService.adminArticleDelete(aid);

        return ResponseEntity.ok().build();
    }

}
