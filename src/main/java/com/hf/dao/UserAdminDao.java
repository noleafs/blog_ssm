package com.hf.dao;

import com.hf.domain.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserAdminDao {

    @Select({"<script>"+"select * from user where 1=1 "+"<if test='q!=null'> and username like concat('%',#{q},'%')  or email like concat('%',#{q},'%')   </if> "+ "</script>"})
    List<User> finAll(@Param("q") String q);

    @Delete("delete from user where uid = #{uid}")
    void adminUserDelete(Integer uid);

    @Select("select uid,head,username,role_id from user where uid = #{uid}")
    public User queryByPrimary(Integer uid);

    @Update("<script>" + "update user set username=#{user.username},email=#{user.email},role_id=#{user.role_id},activate=#{user.activate}"+"<if test='user.password != null'> ,password=#{user.password}  </if>"+" where  uid = #{user.uid} "+"</script>")
    Integer adminUserPut(@Param("user") User user);
}
