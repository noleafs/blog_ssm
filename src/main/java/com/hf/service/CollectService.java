package com.hf.service;

import com.hf.domain.Article;
import com.hf.vo.ArticleVo;
import com.hf.vo.PageResult;

public interface CollectService {
    void Collect(Integer aid, Integer uid);

    void whetherCollect(Integer aid, Integer uid);

    void deleteCollect(Integer aid, Integer uid);

    PageResult<ArticleVo> findAllByUid(Integer page, Integer uid);
}
