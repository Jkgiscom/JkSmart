package com.jkgis.smart;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.jkgis.smart.server.HttpCode;
import com.jkgis.smart.servlet.HtmlServlet;
import com.jkgis.smart.util.JkSmartUtil;


public class JkRequestWork implements Runnable {

    private JkRequest request;
    private SocketChannel channel ;
    private SelectionKey key;
    private HtmlServlet servlet ;
    private ByteBuffer buffer = ByteBuffer.allocate(1024*1024);

    public JkRequestWork(JkRequest request, SelectionKey key, HtmlServlet servlet) {
        this.request = request;
        this.key = key;
        this.channel = (SocketChannel) key.channel();
        this.servlet = servlet;
    }

    public void run() {
        JkResponse response = new JkResponse();
        try {
            servlet.doService(request, response);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
            String msg = e.getMessage();
            msg = msg == null ? "404":msg.trim();
            if("403".equals(msg)){
                response.setCode(HttpCode.STATUS_403.getCode());
                response.setMsg(HttpCode.STATUS_403.getMsg());
            }else if("500".equals(msg)){
                response.setCode(HttpCode.STATUS_500.getCode());
                response.setMsg(HttpCode.STATUS_500.getMsg());
            }else{
                response.setCode(HttpCode.STATUS_404.getCode());
                response.setMsg(HttpCode.STATUS_404.getMsg());
            }

            ByteArrayOutputStream out = response.getOut();
            try {
                InputStream in = new FileInputStream(JkSmartUtil.APP_HOME + "/static/"+msg+".html");
                byte[] bytes = new byte[1024];
                int len;
                while ((len = in.read(bytes)) > -1) {
                    out.write(bytes, 0, len);
                }
                
                in.close();
            } catch (Exception ex) {
                System.out.println("输出"+msg+"的时候发送异常！" + ex.getMessage());
            }
        }
        byte[] rsBody = response.getOut().toByteArray();
        byte[] rsHead = JkParser.responseHeader(response);
        ByteBuffer buff = ByteBuffer.allocate(rsHead.length + rsBody.length);
        buff.put(rsHead);
        buff.put(rsBody);
        buff.flip();
        key.attach(buff);
        key.interestOps(SelectionKey.OP_WRITE);
        key.selector().wakeup();
    }
}
