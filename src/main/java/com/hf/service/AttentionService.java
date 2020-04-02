package com.hf.service;

import com.hf.domain.User;
import com.hf.vo.PageResult;

public interface AttentionService {
    void attention(Integer uid, Integer aUid);

    void whetherAttention(Integer uid,Integer aUid);

    void deleteAttention(Integer uid, Integer aUid);

    PageResult<User> Concern(Integer page, Integer uid);

    PageResult<User> Fans(Integer page, Integer uid);
}
