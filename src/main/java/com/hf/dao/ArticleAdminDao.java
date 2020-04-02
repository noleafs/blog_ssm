package com.hf.dao;

import com.hf.domain.Article;
import com.hf.vo.ArticleManager;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface ArticleAdminDao {


    @Select({"<script>"+"select aid,user_Id,pageview,title,lable_id,tag,releasetime from article where 1=1 "+"<if test='q!=null'> and title like concat('%',#{q},'%')    </if> "+ "</script>"})
    @Results({
            @Result(property = "userName",column = "user_id",javaType = String.class,one = @One(select = "com.hf.dao.UserDao.queryUsernamePrimary")),
            @Result(property = "labelName",column = "lable_id",javaType = String.class,one = @One(select = "com.hf.dao.LabelDao.queryByPrimary",fetchType = FetchType.EAGER)),
    })
    List<ArticleManager> finAll(@Param("q") String q);


    //删除文章
    @Delete("delete from article where aid = #{aid}")
    public Integer deleteAdminById(@Param("aid") Integer aid);


    @Select("select aid,title,`describe`,content,lable_id,tag from article where aid = #{aid}")
    public Article revamp(Integer aid);
}
