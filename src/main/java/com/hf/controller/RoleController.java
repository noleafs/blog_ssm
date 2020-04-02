package com.hf.controller;

import com.hf.domain.Role;
import com.hf.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/admin/all")
    public ResponseEntity<List<Role>> adminAll(){
        return ResponseEntity.ok(roleService.adminAll());
    }
}
