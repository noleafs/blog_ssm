package com.hf.dao;

import com.hf.domain.Article;
import com.hf.domain.User;
import com.hf.vo.ArticleDetails;
import com.hf.vo.ArticleVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.data.redis.connection.ConnectionUtils;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

//文章dao
public interface ArticleDao {

    @Insert("insert into article values(null,#{user_id},0,#{title},#{describe},#{content},#{lable_id},#{tag},#{releasetime})")
    public Integer saveArticle(Article article);

    /**
     * 查询详情
     * @param aid
     * @return
     */
    @Select("select * from article where aid = #{aid}")
    @Results({
            @Result(id = true,property = "aid",column = "aid"),
            @Result(property = "title",column = "title"),
            @Result(property = "pageView",column = "pageview"),
            @Result(property = "content",column = "content"),
            @Result(property = "releaseTime",column = "releasetime"),
            @Result(property = "labelString",column = "lable_id",javaType = String.class,one = @One(select = "com.hf.dao.LabelDao.queryByPrimary",fetchType = FetchType.EAGER)),
            @Result(property = "user",column = "user_id",javaType = User.class,one = @One(select = "com.hf.dao.UserDao.queryCountByPrimary",fetchType = FetchType.EAGER)),
            @Result(property = "commentList",column = "aid",javaType = List.class,many = @Many(select = "com.hf.dao.CommentDao.queryListByAid")),
    })
    public ArticleDetails queryById(Integer aid);

    /**
     * 文章列表
     * @return
     * @param uid
     */
//    @Select({"<script>"+"select * from article where 1=1 "+"<if test='uid!=null'> and user_id = #{uid} </if> "+"  ORDER BY releasetime DESC" + "</script>"})
    @SelectProvider(type = JoinFindAll.class,method = "findAll")
    @Results({
            @Result(id = true,property = "aid",column = "aid"),
            @Result(property = "pageview",column = "pageview"),
            @Result(property = "title",column = "title"),
            @Result(property = "describe",column = "describe"),
            @Result(property = "releasetime",column = "releasetime"),
            @Result(property = "username",column = "user_id",javaType = String.class,one = @One(select = "com.hf.dao.UserDao.queryUsernamePrimary")),
            @Result(property = "imgPath",column = "tag",javaType = String.class,one = @One(select = "com.hf.dao.PictureDao.searchPath"))
    })
    public List<ArticleVo> findAll(@Param("uid") Integer uid,@Param("lid") Integer lid, @Param("q") String q,@Param("sort") Integer sort,@Param("ids") List<Integer> ids);

    @Select("select aid,title from article ORDER BY pageview DESC limit 8")
    List<ArticleVo> hot();


    class JoinFindAll{

        public String findAll(@Param("uid") Integer uid,@Param("lid") Integer lid, @Param("q") String q,@Param("sort") Integer sort,@Param("ids") List<Integer> ids){
            String sql = "select * from article where 1=1 ";
            //<if test='uid!=null'> and user_id = #{uid} </if>  ORDER BY releasetime DESC
            if (uid != null) {
                sql += (" and user_id = " + uid);
            }
            if (lid !=null) {
                sql += (" and lable_id = " + lid);
            }
            if (StringUtils.isNotEmpty(q)){
                sql += " and title like '%"+q+"%' ";
            }

            // 关注用户
            if (sort == 3){
                String s = " and user_id in (";
                if (!CollectionUtils.isEmpty(ids)) {
                    for (Integer id : ids) {
                        s += id + ",";
                    }
                    s = StringUtils.stripEnd(s, ",");
                    s += ")";
                }else{
                    s += "99999999)";
                }


                sql += s;

            }

            //按照发布时间排序
            if (sort == 1){
                sql += " ORDER BY releasetime DESC";
            }else if (sort == 2){
                //按照热度排序
                sql += " ORDER BY pageview DESC";
            }

            System.out.println(sql);
            return sql;
        }

    }


    @SelectProvider(type = JoinSql.class,method = "find")
    @Results({
            @Result(id = true,property = "aid",column = "aid"),
            @Result(property = "pageview",column = "pageview"),
            @Result(property = "title",column = "title"),
            @Result(property = "describe",column = "describe"),
            @Result(property = "releasetime",column = "releasetime"),
            @Result(property = "username",column = "user_id",javaType = String.class,one = @One(select = "com.hf.dao.UserDao.queryUsernamePrimary")),
            @Result(property = "imgPath",column = "tag",javaType = String.class,one = @One(select = "com.hf.dao.PictureDao.searchPath"))
    })
    public List<ArticleVo> findAllByAid(List<Integer> listAid);

    class JoinSql{
        public String find(Map map){
            String sql = "select * from article where aid in ";
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



    @Update("update article set title=#{article.title},lable_id=#{article.lable_id},`describe`=#{article.describe},content=#{article.content}  where aid = #{aid} and user_id = #{article.user_id} ")
    Integer updateArticle(@Param("aid") Integer aid, @Param("article") Article article);


    @Update("update article set pageview = pageview + 1 where aid = #{aid}")
    public void updatePageView(Integer aid);


    @Select("select count(aid) from article where user_id = #{uid}")
    public Integer queryCountByUid(Integer uid);

    //删除文章
    @Delete("delete from article where user_id = #{uid} and aid = #{aid}")
    public Integer deleteById(@Param("uid")Integer uid,@Param("aid") Integer aid);

    @Select("select aid,title,`describe`,content,lable_id,tag from article where aid = #{aid} and user_id = #{uid}")
    public Article revamp(@Param("aid")Integer aid,@Param("uid") Integer uid);
}
