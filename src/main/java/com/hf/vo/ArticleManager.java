package com.hf.vo;

import com.hf.domain.Article;
import lombok.Data;

@Data
public class ArticleManager extends Article {
    private String userName;
    private String labelName;
}
