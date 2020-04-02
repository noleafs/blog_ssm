package com.hf.service.impl;

import com.hf.dao.RoleAdminDao;
import com.hf.domain.Role;
import com.hf.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleAdminDao roleAdminDao;

    @Override
    public List<Role> adminAll() {

        return roleAdminDao.adminAll();
    }

}
