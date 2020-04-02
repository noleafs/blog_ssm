package com.hf.vo;

import lombok.Data;

import java.util.Map;

@Data
public class SMB {
    private String server;
    private String database;
    private String time;
    private String ip;
    private String ram;
    private Map<String,String> rom;
    private String jdk;
}
