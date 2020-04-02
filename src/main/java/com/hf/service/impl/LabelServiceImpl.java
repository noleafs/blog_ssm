package com.hf.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hf.common.exception.HfException;
import com.hf.dao.LabelAdminDao;
import com.hf.dao.LabelDao;
import com.hf.domain.Label;
import com.hf.service.LabelService;
import com.hf.vo.ArticleVo;
import com.hf.vo.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LabelServiceImpl implements LabelService {

    private static final String hotPrefix = "hot_label";

    @Autowired
    private LabelDao labelDao;

    @Autowired
    private LabelAdminDao labelAdminDao;

    @Autowired
    private StringRedisTemplate redisTemplate;


    @Value("${hf.hot.cache}")
    private Integer hotCache;

    @Override
    public List<Label> label() {

        return labelDao.findAll();
    }

    @Override
    public List<Label> hotLabel() {
        ObjectMapper mapper = new ObjectMapper();
        String json;

        //判断redis中是否存在缓存
        json = redisTemplate.opsForValue().get(hotPrefix);
        if (StringUtils.isNotEmpty(json)){
            try {
                List<Label> labels = mapper.readValue(json, new TypeReference<List<Label>>() {});
                return labels;
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        }

        List<Label> labels = labelDao.hotLabel();

        //加入缓存
        //存储redis
        try {
            json = mapper.writeValueAsString(labels);
            redisTemplate.opsForValue().set(hotPrefix,json,hotCache, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }


        return  labels;
    }

    @Override
    public PageResult<Label> adminLabelAll(Integer page, String q) {
        PageHelper.startPage(page,10);

        List<Label> labels = labelAdminDao.finAll(q);

        PageInfo<Label> info = new PageInfo<>(labels);

        return new PageResult<>(info.getTotal(),(long)info.getPages(),labels);
    }


    @Override
    public void adminLabelAdd(String name) {
        Integer i = labelAdminDao.adminLabelAdd(name, System.currentTimeMillis());
        if (i != 1){
            throw new HfException(400,"添加失败");
        }
    }

    @Override
    public void adminLabelPut(Map<String, String> map) {
        labelAdminDao.adminLabelUpdate(map);
    }
}
