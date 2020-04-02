package com.hf.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.hf.common.exception.HfException;
import com.hf.dao.ArticleAdminDao;
import com.hf.dao.ArticleDao;
import com.hf.dao.AttentionDao;
import com.hf.dao.PictureDao;
import com.hf.domain.Article;
import com.hf.domain.Comment;
import com.hf.domain.Picture;
import com.hf.service.ArticleService;
import com.hf.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final String hotPrefix = "hot_article";

    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private PictureDao pictureDao;

    @Autowired
    private AttentionDao attentionDao;

    @Autowired
    private ArticleAdminDao articleAdminDao;

    @Autowired
    private FastFileStorageClient storageClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${hf.url}")
    private String url;

    @Value("${hf.hot.cache}")
    private Integer hotCache;

    @Override
    @Transactional
    public ImageVo imageUpload(MultipartFile file, String tag) {
        String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
        ImageVo imageVo = new ImageVo();
        try {
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            imageVo.setUrl(url+storePath.getFullPath());
            imageVo.setSuccess(1);
            imageVo.setMessage("上传成功");


            // 保存图片信息
            pictureDao.savePicture(new Picture(null,tag,storePath.getFullPath(),System.currentTimeMillis()));

        } catch (IOException e) {
            e.printStackTrace();
            imageVo.setSuccess(0);
            imageVo.setMessage("上传失败");
        }
        return imageVo;
    }

    @Override
    public void article(Article article) {
        article.setReleasetime(System.currentTimeMillis());
        //保存文章
        Integer i = articleDao.saveArticle(article);

        //将标记设为1
        String tag = article.getTag();
        pictureDao.updateByTag(tag);

        if (i != 1){
            throw new HfException(400,"发布失败");
        }

    }

    @Override
    @Transactional
    public ArticleDetails queryById(Integer aid) {
        articleDao.updatePageView(aid);

        ArticleDetails articleDetails = articleDao.queryById(aid);

        String path = articleDetails.getUser().getHead();
        articleDetails.getUser().setHead(url + path);
//        System.out.println("--------------------------------------------------------");
        //根评论
        List<Comment> commentList = articleDetails.getCommentList();

        int i = 0;
        //遍历根评论
        for (Comment comment : commentList) {

            // 填充根评论完整地址
            String head = comment.getUser().getHead();
            if (!StringUtils.containsIgnoreCase(head,url)){
                comment.getUser().setHead(url + head);
            }


//            System.out.println("=============================第"+i+"条根评论下的所有回复===============================");
            List<Comment> replyList = comment.getReplyList();

            // 填充图片完整地址
            for (Comment comment1 : replyList) {
                head = comment1.getUser().getHead();
                // 是否包含前缀
                if (!StringUtils.containsIgnoreCase(head,url)){
                    comment1.getUser().setHead(url + head);
                }

            }

            //循环所有回复，但没有层级关系
            int size = replyList.size(); //每个跟评论有这么多个回复

            // 如果根评论下没有回复 直接跳过本次循环
            if (size == 0) {
                continue;
            }

            //存储层级关系
            List<List<List<Comment>>>  hierarchy = new ArrayList<>();
            List<Integer> ids = new ArrayList<>();  //[1,3,4,5,6]
            ids.add(comment.getCid());  //首先从跟评论开始排序层级关系 首先赋值根评论的id  根据此id查询他的所有回复者     cid

            int s = 0;
            //回复有多少个就循环多少次
            for (int r = 0; r < size; r++) {
                int count = ids.size(); // 3
                    List<List<Comment>> cj = new ArrayList<>();
                    for (int j = s; j < count; j++) {  // 4 6 每循环完 代表一层
                        //将回复者存储到此list里
                        List<Comment> comments = new ArrayList<>();
                        for (Comment rep : replyList) {
                            if (rep.getCid_id() == ids.get(j)){
                                comments.add(rep);
                            }
                        }
                        if (comments.size() > 0){
                            cj.add(comments);
                        }

                    }
                    if (cj.size() > 0){
                        for (List<Comment> comments : cj) {
                            for (Comment comment1 : comments) {
                                ids.add(comment1.getCid());
                            }
                        }
                        System.out.println("ids为："+ids); // [3, 4, 6]
                        hierarchy.add(cj);
                    }

                    s = count; //从第几位开始

            }

            ++i;

//            for (List<List<Comment>> lists : hierarchy) {
//                for (List<Comment> list : lists) {
//                    System.out.print(list);
//                }
//                System.out.println();
//            }
//            System.out.println("排序前");

            for (int j = hierarchy.size() - 1; j > 0 ; j--) {
                for (List<Comment> comments : hierarchy.get(j-1)) {
                    for (Comment comment1 : comments) {
                        for (List<Comment> list : hierarchy.get(j)) {  // 最后一层
                            for (Comment comment2 : list) {
                                if (comment1.getCid() == comment2.getCid_id()) {
                                    List<Comment> replyList1 = comment1.getReplyList();
                                    replyList1.add(comment2);
                                }
                            }
                        }

                    }
                }
            }
//            System.out.println("排序后");
            List<List<Comment>> lists = hierarchy.get(0);
            for (List<Comment> list : lists) {
               comment.setReplyList(list);
            }

        }

//        System.out.println("--------------------------------------------------------");
        return articleDetails;
    }

    @Override
    public PageResult<ArticleVo> findAll(String page, Integer uid, Integer lid, String q, Integer sort,Integer userId,Integer size) {
        //分页
        PageHelper.startPage(Integer.valueOf(page),size);
        // 如果排序为3 需要查询出当前登录用户的关注用户的id
        List<Integer> ids = null;
        if (sort == 3){
           ids = attentionDao.findFollowCount(userId);
        }


        List<ArticleVo> articleVoList = articleDao.findAll(uid, lid, q,sort,ids);
        if (CollectionUtils.isEmpty(articleVoList)){
            return null;
        }

        //打乱顺序
        if (sort!= null && sort == 4){
            Collections.shuffle(articleVoList);
            Collections.shuffle(articleVoList);
        }
        //填充完整url
        for (ArticleVo articleVo : articleVoList) {
            if (articleVo.getImgPath() != null) {
                articleVo.setImgPath(url + articleVo.getImgPath());
            }

        }
        PageInfo<ArticleVo> info = new PageInfo<>(articleVoList);
        return new PageResult<ArticleVo>(info.getTotal(), (long) info.getPages(), articleVoList);
    }

    @Override
    @Transactional
    public void deleteById(Integer uid, Integer aid) {
        String tag = articleDao.revamp(aid, uid).getTag();
        List<String> paths = pictureDao.queryPathByTag(tag);

        //删除用户上传时的照片
        for (String path : paths) {
            storageClient.deleteFile(path);
        }

        Integer i = pictureDao.deleteByTag(tag);
        if (paths.size() != i){
            throw new HfException(400,"删除失败");
        }

        i = articleDao.deleteById(uid, aid);
        if (i != 1){
            throw new HfException(400,"删除失败");
        }

    }

    @Override
    public Article revamp(Integer aid, Integer uid) {
        Article revamp = articleDao.revamp(aid, uid);
        if (revamp == null){
            throw new HfException(400,"发生异常");
        }
        return revamp;
    }

    @Override
    public void updateArticle(Integer aid, Article article) {
        Integer i = articleDao.updateArticle(aid, article);
        if (i != 1){
            throw new HfException(400,"修改失败");
        }
    }

    @Override
    public List<ArticleVo> hot(){
        ObjectMapper mapper = new ObjectMapper();
        String json;

        //判断redis中是否存在缓存
        json = redisTemplate.opsForValue().get(hotPrefix);
        if (StringUtils.isNotEmpty(json)){
            try {
                List<ArticleVo> articleVos = mapper.readValue(json, new TypeReference<List<ArticleVo>>() {});
                return articleVos;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }

        List<ArticleVo> articleVos = articleDao.hot();
        //存储redis
        try {
            json = mapper.writeValueAsString(articleVos);
            redisTemplate.opsForValue().set(hotPrefix,json,hotCache, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return articleVos;
    }

    @Override
    public PageResult<ArticleManager> adminArticleAll(Integer page, String q) {
        PageHelper.startPage(page,10);
        List<ArticleManager> articleManagerList = articleAdminDao.finAll(q);

        PageInfo<ArticleManager> info = new PageInfo<>(articleManagerList);

        return new PageResult<>(info.getTotal(),(long)info.getPages(),articleManagerList);
    }

    @Override
    public void adminArticleDelete(Integer aid) {


        String tag = articleAdminDao.revamp(aid).getTag();
        List<String> paths = pictureDao.queryPathByTag(tag);

        //删除用户上传时的照片
        for (String path : paths) {
            storageClient.deleteFile(path);
        }

        Integer i = pictureDao.deleteByTag(tag);
        if (paths.size() != i){
            throw new HfException(400,"删除失败");
        }


        i = articleAdminDao.deleteAdminById(aid);
        if (i != 1){
            throw new HfException(400,"删除失败");
        }
    }

}
