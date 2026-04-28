package com.jkgis.smart.server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jkgis.smart.util.JkSmartUtil;

public class Mine {
    private static Map<String, String> MINE = null;
    private Mine(){
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(JkSmartUtil.APP_HOME + "/conf/mine.json"), "UTF-8"));
            StringBuilder jsonstr = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                jsonstr.append(line);
            }
            in.close();
            JSONObject mine = JSON.parseObject(jsonstr.toString());
            MINE = new HashMap<>();
            mine.entrySet().forEach(entry ->{
                MINE.put(entry.getKey(), (String)entry.getValue());
                MINE.put(entry.getKey().toUpperCase(), (String)entry.getValue());
            });
            System.out.println("初始化mime.json成功..");
        } catch (Exception e) {
            System.out.println("初始化mime.json失败！"+e.getMessage());
        }
    }

    public static String getMine(String key){
        if(MINE==null){
            new Mine();
        }
        return MINE.get(key);
    }

    public static void main(String[] args){
        System.out.println(Mine.getMine("css"));
    }
}
