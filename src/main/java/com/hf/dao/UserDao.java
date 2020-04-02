package com.hf.dao;

import com.hf.domain.User;
import com.hf.vo.HomePage;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

//用户dao
public interface UserDao {

    @Insert("insert into user values(null,#{username},#{password},#{email},#{head},#{registration},#{activate},3)")
    public int saveUser(User user);

    @Select("select * from user where uid = #{uid}")
    public User userByUid(Integer uid);

    @Select("select * from user where email = #{email}")
    public User queryByEmail(String email);

    @Update("update user set activate=1 where email = #{email} ")
    public void updateByEmail(String email);

    @Select("select count(uid) from user where username = #{username} limit 1 ")
    public Integer queryCountByUsername(@Param("username") String username);

    @Select("select count(uid) from user where email = #{email} limit 1 ")
    public Integer queryCountByEmail(@Param("email") String email);

    @Select("select uid,head,username from user where uid = #{uid}")
    public User queryByPrimary(Integer uid);

    @Select("select uid,head,username from user where uid = #{uid}")
    @Results({
            @Result(id = true,property = "uid",column = "uid"),
            @Result(property = "head",column = "head"),
            @Result(property = "username",column = "username"),
            @Result(property = "username",column = "username"),
            @Result(property = "NumberOfConcerns",column = "uid",javaType = Integer.class,one = @One(select = "com.hf.dao.AttentionDao.queryBeCountByUid")),
            @Result(property = "ConcernNumber",column = "uid",javaType = Integer.class,one = @One(select = "com.hf.dao.AttentionDao.queryConcernCountByUid")),
    })
    public User queryCountByPrimary(Integer uid);


    @Select("select username from user where uid = #{uid}")
    public String queryUsernamePrimary(Integer uid);

    @Select("select * from user where uid = #{uid}")
    @Results({
            @Result(property = "fansCount",column = "uid",javaType = Integer.class,one = @One(select = "com.hf.dao.AttentionDao.queryBeCountByUid")),
            @Result(property = "payCount",column = "uid",javaType = Integer.class,one = @One(select = "com.hf.dao.AttentionDao.queryConcernCountByUid")),
            @Result(property = "articleCount",column = "uid",javaType = Integer.class,one = @One(select = "com.hf.dao.ArticleDao.queryCountByUid")),
            @Result(property = "collectCount",column = "uid",javaType = Integer.class,one = @One(select = "com.hf.dao.CollectDao.queryCountByUid")),
    })
    public HomePage queryHomePage(Integer uid);


    @SelectProvider(type = JoinSql.class,method = "find")
    public List<User> findAllByUid(List<Integer> listUid);

    class JoinSql{
        public String find(Map map){
            String sql = "select uid,username,head from user where uid in ";
            Set set = map.keySet();
            List<Integer> list = (List<Integer>) map.get("list");
            String k = "(";
            for (Integer id : list) {
                k += id + ",";
            }
            k = StringUtils.stripEnd(k, ",");
            k += ")";
            sql += k;
            return sql;
        }
    }

    @Update("update user set username=#{username},password=#{password},email=#{email},head=#{head} where uid = #{uid} ")
    public Integer userUpdate(User user);

    @Update("update user set password=#{password} where email=#{email} ")
    public Integer retrievePass(User user);

}
