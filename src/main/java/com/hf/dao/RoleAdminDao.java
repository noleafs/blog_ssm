package com.hf.dao;

import com.hf.domain.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleAdminDao {


    @Select("select rid,role_name from role")
    List<Role> adminAll();


}
