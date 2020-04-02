package com.hf.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hf.common.exception.HfException;
import com.hf.dao.ArticleDao;
import com.hf.dao.CollectDao;
import com.hf.domain.Article;
import com.hf.service.CollectService;
import com.hf.vo.ArticleVo;
import com.hf.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CollectServiceImpl implements CollectService {

    @Autowired
    private CollectDao collectDao;

    @Autowired
    private ArticleDao articleDao;

    @Value("${hf.url}")
    private String url;


    @Override
    public void Collect(Integer aid, Integer uid) {
        long time = new Date().getTime();
        collectDao.saveCollect(aid,uid,time);
    }

    @Override
    public void whetherCollect(Integer aid, Integer uid) {
        Integer count = collectDao.whetherCollect(aid, uid);
        if (count != 1){
            throw new HfException(204,"用户未收藏该文章");
        }
    }

    @Override
    public void deleteCollect(Integer aid, Integer uid) {
        collectDao.deleteAttention(aid,uid);
    }

    @Override
    public PageResult<ArticleVo> findAllByUid(Integer page, Integer uid) {
        PageHelper.startPage(page,6);
        List<Integer> ids = collectDao.findAllByUid(uid);
        if (CollectionUtils.isEmpty(ids)){
            return null;
        }

        List<ArticleVo> allByAid = articleDao.findAllByAid(ids);
        //填充完整url
        for (ArticleVo articleVo : allByAid) {
            if (articleVo.getImgPath() != null){
                articleVo.setImgPath(url + articleVo.getImgPath());
            }

        }

        PageInfo<ArticleVo> info = new PageInfo<>(allByAid);

        //                          总条数          总页数                 当前页数据
        return new PageResult<>(info.getTotal(), (long) info.getPages(),allByAid);
    }
}
