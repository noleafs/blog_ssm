package com.hf.service;

import com.hf.domain.Label;
import com.hf.vo.PageResult;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface LabelService {

    List<Label> label();

    List<Label> hotLabel();

    PageResult<Label> adminLabelAll(Integer page, String q);

    void adminLabelAdd(String name);

    void adminLabelPut(Map<String, String> map);
}
