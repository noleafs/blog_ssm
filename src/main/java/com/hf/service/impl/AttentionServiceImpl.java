package com.hf.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hf.common.exception.HfException;
import com.hf.dao.AttentionDao;
import com.hf.dao.UserDao;
import com.hf.domain.Attention;
import com.hf.domain.User;
import com.hf.service.AttentionService;
import com.hf.vo.ArticleVo;
import com.hf.vo.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class AttentionServiceImpl implements AttentionService {

    @Autowired
    private AttentionDao attentionDao;

    @Autowired
    private UserDao userDao;

    @Value("${hf.url}")
    private String url;

    @Override
    public void attention(Integer uid, Integer aUid) {
        //整合数据
        Attention attention = new Attention(null,uid,aUid,System.currentTimeMillis());

        Integer i = attentionDao.saveAttention(attention);
        if (i != 1){
            throw new HfException(400,"已关注");
        }

    }

    @Override
    public void whetherAttention(Integer uid, Integer aUid) {
        Integer count = attentionDao.whetherAttention(uid, aUid);
        if (count != 1){
            throw new HfException(204,"未关注");
        }

    }

    @Override
    public void deleteAttention(Integer uid, Integer aUid) {
        attentionDao.deleteAttention(uid,aUid);
    }

    @Override
    public PageResult<User> Concern(Integer page, Integer uid) {
        PageHelper.startPage(page,6);
        List<Integer> followCount = attentionDao.findFollowCount(uid);
        if (CollectionUtils.isEmpty(followCount)) {
            return null;
        }
        List<User> allByUser = userDao.findAllByUid(followCount);

        // 填充完整路径
        for (User user : allByUser) {
            if (user.getHead() != null){
                user.setHead(url + user.getHead());
            }

        }
        PageInfo<User> info = new PageInfo<>(allByUser);
        return new PageResult<User>(info.getTotal(), (long) info.getPages(),allByUser);
    }

    @Override
    public PageResult<User> Fans(Integer page, Integer uid) {
        PageHelper.startPage(page,6);
        List<Integer> beCount = attentionDao.findBeCount(uid);
        if (CollectionUtils.isEmpty(beCount)) {
            return null;
        }
        List<User> allByUser = userDao.findAllByUid(beCount);

        //填充完整url
        for (User user : allByUser) {
            if (user.getHead() != null){
                user.setHead(url + user.getHead());
            }

        }
        PageInfo<User> info = new PageInfo<>(allByUser);
        return new PageResult<User>(info.getTotal(), (long) info.getPages(),allByUser);

    }





}
