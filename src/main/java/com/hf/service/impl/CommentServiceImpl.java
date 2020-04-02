package com.hf.service.impl;

import com.hf.dao.CommentDao;
import com.hf.domain.Comment;
import com.hf.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentDao commentDao;

    @Override
    public void Comment(Comment comment) {
        commentDao.saveComment(comment);
    }
}
