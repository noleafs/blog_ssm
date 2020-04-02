package com.hf.dao;

import com.hf.domain.Label;
import com.hf.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.Map;

public interface LabelAdminDao {

    @Select({"<script>"+"select * from label where 1=1 "+"<if test='q!=null'> and classname like concat('%',#{q},'%')  </if> "+ "</script>"})
    List<Label> finAll(@Param("q") String q);

    @Insert("insert into label values(null,#{name},#{t})")
    Integer adminLabelAdd(@Param("name") String name,@Param("t") Long time);

    @Update("update label set classname = #{text} where id = #{id} ")
    Integer adminLabelUpdate(Map<String,String> map);
}
