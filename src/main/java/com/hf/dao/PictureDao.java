package com.hf.dao;

import com.hf.domain.Article;
import com.hf.domain.Picture;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

//图片dao
public interface PictureDao {

    @Insert("insert into picture values(null,#{article_tag},#{path},#{picturetime},#{orphan})")
    public void savePicture(Picture picture);

    @Select("select path from picture where article_tag = #{tag} limit 1 ")
    public String searchPath(String tag);

    @Select("select path from picture where article_tag = #{tag}")
    public List<String> queryPathByTag(String tag);

    @Delete("delete from picture where article_tag = #{tag}")
    public Integer deleteByTag(String tag);

    @Update("update picture set orphan = 1 where article_tag = #{tag}")
    public void updateByTag(String tag);
}
