package com.hf.dao;

import com.hf.domain.Attention;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

public interface AttentionDao {

    /**
     * 查询被关注总数
     * @param uid
     * @return
     */
    @Select("select count(aid) count from attention where follow = #{uid}")
    public Integer queryBeCountByUid(Integer uid);

    /**
     * 查询关注数
     * @param uid
     * @return
     */
    @Select("select count(aid) count from attention where befocused = #{uid}")
    public Integer queryConcernCountByUid(Integer uid);


    /**
     * 关注
     * @param attention
     * @return
     */
    @Insert("insert into attention values(null,#{befocused},#{follow},#{attentionTime})")
    public Integer saveAttention(Attention attention);

    /**
     * 是否关注
     * @param uid
     * @param aUid
     * @return
     */
    @Select("select count(aid) from attention where befocused = #{uid} and follow = #{aUid}")
    public Integer whetherAttention(@Param("uid") Integer uid, @Param("aUid") Integer aUid);

    /**
     * 取消关注
     * @param uid
     * @param aUid
     */
    @Delete("delete from attention where befocused = #{uid} and follow = #{aUid}")
    void deleteAttention(@Param("uid") Integer uid, @Param("aUid") Integer aUid);

    /**
     * 通过当前登录用户，查询出所有关注他的用户的id
     * @param uid
     * @return
     */
    @Select("select befocused from attention where follow = #{uid}")
    List<Integer> findBeCount(Integer uid);

    /**
     * 通过当前登录用户，查询出所有关注的用户
     * @param uid
     * @return
     */
    @Select("select follow from attention where befocused = #{uid}")
    List<Integer> findFollowCount(Integer uid);
}
