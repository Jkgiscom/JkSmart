package com.jkgis.smart;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jkgis.smart.server.JkSmartNioServer;
import com.jkgis.smart.server.Mine;
import com.jkgis.smart.util.JkSmartUtil;


public class JkSmartServer {
    
    public JkSmartServer(){}

    public static void main(String[] args) {
        System.out.println("启动Jk-Http-smart服务器.."+new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒").format(new Date()));
        System.out.println("主目录："+JkSmartUtil.getString("static_home"));
        System.out.println("端口："+JkSmartUtil.getIntValue("port"));
        Mine.getMine("css");
        new JkSmartNioServer().server();
    }
}
