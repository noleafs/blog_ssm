package com.hf.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//收藏dao
public interface CollectDao {

    @Insert("insert into collect values(null,#{uid},#{aid},#{time})")
    void saveCollect(@Param("aid") Integer aid,@Param("uid") Integer uid, @Param("time") Long time);

    @Select("select count(cid) count from collect where user_id = #{uid} and article_id = #{aid} limit 1")
    Integer whetherCollect(@Param("aid") Integer aid,@Param("uid") Integer uid);

    /**
     * 取消收藏
     * @param aid
     * @param uid
     */
    @Delete("delete from collect where user_id = #{uid} and article_id = #{aid}")
    void deleteAttention(@Param("aid") Integer aid, @Param("uid") Integer uid);


    @Select("select count(cid) from collect where user_id = #{uid}")
    Integer queryCountByUid(Integer uid);


    /**
     * 查询用户收藏的文章id
     * @param uid
     * @return
     */
    @Select("select article_id from collect where user_id = #{uid}")
    List<Integer> findAllByUid(Integer uid);
}
