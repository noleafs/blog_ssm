package com.hf.controller;

import com.hf.domain.Label;
import com.hf.service.LabelService;
import com.hf.vo.ArticleManager;
import com.hf.vo.PageResult;
import com.hf.vo.SMB;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletContext;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class LabelController {

    @Autowired
    private LabelService labelService;

    @GetMapping("/label")
    public ResponseEntity<List<Label>> label(){
        return ResponseEntity.ok(labelService.label());
    }

    @GetMapping("/hotlabel")
    public ResponseEntity<List<Label>> hotLabel(){

        return ResponseEntity.ok(labelService.hotLabel());
    }


    @GetMapping("/admin/server")
    public ResponseEntity<SMB> server() throws Exception{
        SMB smb = new SMB();

        //获得rom
        File[] disks = File.listRoots();
        Map<String,String> map = new HashMap<>();
        for(File file : disks)
        {
            map.put(file.getPath(),file.getFreeSpace() / 1024 / 1024 + "M");
        }
        smb.setRom(map);

        //获得ram
        OperatingSystemMXBean mem = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        smb.setRam(mem.getFreePhysicalMemorySize() / 1024 / 1024 + "MB");


        //Ip
        InetAddress addr = InetAddress.getLocalHost();
        smb.setIp(addr.getHostAddress());

        //系统
        smb.setServer(System.getProperties().getProperty("os.name"));

        //时间
        String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        smb.setTime(date);

        //数据库
        smb.setDatabase("Mysql");

        //JDK版本
        Properties props = System.getProperties();
        String property = props.getProperty("java.version");
        smb.setJdk(property);

        return ResponseEntity.ok(smb);
    }


    //-----------------------------------------------------------------------------------------------

    /**
     * 查询所有标签
     * @return
     */
    @GetMapping("/label/admin/all")
    public ResponseEntity<PageResult<Label>> adminLabelAll(@RequestParam(value = "page",defaultValue = "1") Integer page, String q){

        return ResponseEntity.ok(labelService.adminLabelAll(page,q));
    }


    /**
     * 添加标签
     * @param map
     * @return
     */
    @PostMapping("/label/admin")
    public ResponseEntity<Void> adminLabelAdd(@RequestBody Map<String,String> map){
        labelService.adminLabelAdd(map.get("name"));
        return ResponseEntity.ok().build();
    }

    /**
     * 修改标签
     * @param id
     * @param map
     * @return
     */
    @PutMapping("/label/admin/{id}")
    public ResponseEntity<Void> adminLabelPut(@PathVariable(value = "id",required = true) Integer id,@RequestBody Map<String,String> map){
        labelService.adminLabelPut(map);
        return ResponseEntity.ok().build();
    }



}

