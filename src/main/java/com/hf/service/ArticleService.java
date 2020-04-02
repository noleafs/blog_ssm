package com.hf.service;

import com.hf.domain.Article;
import com.hf.vo.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ArticleService {
    ImageVo imageUpload(MultipartFile file, String tag);

    void article(Article a);

    ArticleDetails queryById(Integer aid);

    PageResult<ArticleVo> findAll(String page, Integer uid,Integer lid,String q,Integer sort,Integer userId,Integer size);

    void deleteById(Integer uid, Integer aid);

    Article revamp(Integer aid, Integer uid);

    void updateArticle(Integer aid, Article article);

    List<ArticleVo> hot();

    PageResult<ArticleManager> adminArticleAll(Integer page, String q);

    void adminArticleDelete(Integer aid);
}
