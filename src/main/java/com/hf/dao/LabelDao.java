package com.hf.dao;

import com.hf.domain.Label;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LabelDao {

    @Select("select id,classname from label")
    public List<Label> findAll();

    @Select("select classname from label where id = #{id}")
    public String queryByPrimary(Integer id);

    //取出热门前20条的标签
    @Select("select * from label where id in (select lable_id from (select * from article ORDER BY pageview DESC limit 20) arti  GROUP BY lable_id)")
    public List<Label> hotLabel();
}
