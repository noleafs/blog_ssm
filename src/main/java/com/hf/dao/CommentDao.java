package com.hf.dao;

import com.hf.domain.Comment;
import com.hf.domain.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface CommentDao {

    //根据文章id先查询根评论
    @Select("select * from comment where article_id = #{aid} and cid_id = 0 ")
    @Results({
            @Result(id = true,property = "cid",column = "cid"),
            @Result(property = "user",column = "user_id",javaType = User.class,one = @One(select = "com.hf.dao.UserDao.queryByPrimary")),
            //根据根评论id 去查询下面的所有回复
            @Result(property = "replyList",column = "cid",javaType = List.class,many = @Many(select = "com.hf.dao.CommentDao.queryListByRootId"))
    })
    List<Comment> queryListByAid(Integer aid);


    //根据 根评论id 查询下面的所有回复
    @Select("select * from comment where rootid = #{cid} ORDER BY commentTime ASC")
    @Results({
            @Result(property = "user",column = "user_id",javaType = User.class,one = @One(select = "com.hf.dao.UserDao.queryByPrimary"))
    })
    List<Comment> queryListByRootId(Integer cid);


    @Insert("insert into comment values(null,#{article_id},#{user_id},#{commentContent},#{commentTime},#{cid_id},#{rootid})")
    Integer saveComment(Comment comment);
}
