package com.jkgis.smart.util;

import java.io.*;

import com.alibaba.fastjson.*;
import com.jkgis.smart.JkSmartServer;

public class JkSmartUtil {

    public static String APP_HOME = new File(JkSmartServer.class.getResource("/").getPath()).getParent();
    private static JSONObject cfg;

    private JkSmartUtil() {
        try {
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(new FileInputStream(APP_HOME + "/conf/smart.json"), "UTF-8"));
            StringBuilder jsonstr = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                jsonstr.append(line);
            }
            in.close();
            cfg = JSON.parseObject(jsonstr.toString());
            System.out.println("初始化smart.json成功..");
        } catch (Exception e) {
            System.out.println("初始化smart.json失败！" + e.getMessage());
        }
    }

    public static String getString(String key) {
        if (cfg == null)
            new JkSmartUtil();
        return cfg.getString(key);
    }

    public static int getIntValue(String key) {
        if (cfg == null)
            new JkSmartUtil();
        return cfg.getIntValue(key);
    }

    public static void main(String[] args) {
        // new JkUtil();
        System.out.println(JkSmartUtil.getIntValue("port"));
    }
}