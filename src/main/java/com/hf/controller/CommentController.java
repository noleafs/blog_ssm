package com.hf.controller;

import com.hf.common.exception.HfException;
import com.hf.domain.Comment;
import com.hf.domain.User;
import com.hf.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     * @param cid_id
     * @param rootId
     * @param aid
     * @param commentContent
     * @param request
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> Comment(
            @RequestParam(value = "cid",defaultValue = "0") Integer cid_id,
            @RequestParam(value = "rid",defaultValue = "0") Integer rootId,
            @RequestParam(value = "aid",required = true) Integer aid,
            @RequestParam(value = "commentContent") String commentContent,
            HttpServletRequest request
            )
    {
        User user = (User)request.getSession().getAttribute("user");

        //整合数据
        Comment comment = new Comment();
        comment.setArticle_id(aid);
        comment.setUser_id(user.getUid());
        comment.setCommentContent(commentContent);
        comment.setCommentTime(System.currentTimeMillis());
        comment.setCid_id(cid_id);
        comment.setRootid(rootId);

        commentService.Comment(comment);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
