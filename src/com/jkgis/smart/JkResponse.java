package com.jkgis.smart;

import java.io.*;
import java.util.*;

import com.jkgis.smart.server.HttpCode;



public class JkResponse {
    private String html;
    private Integer code;
    private String protocol = "HTTP/1.1";
    private String msg;
    private Map<String, String> headers;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();

    public JkResponse(){
        this.code = HttpCode.STATUS_200.getCode();
        this.msg = HttpCode.STATUS_200.getMsg();
        headers = new HashMap<>();
        headers.put("content-type", "text/html;charset=UTF-8");
        headers.put("Server", "JkHttpServer v0.1");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Method", "*");
        headers.put("Access-Control-Allow-Headers", "*");
        //headers.put("Keep-Alive", "timeout=60,max=127");
        //headers.put("Transfer-Encoding", "chunked");
    }

    public String getHtml() {
        return html;
    }

    protected void setHtml(String html) {
        this.html = html;
    }

    protected void setCode(Integer code) {
        this.code = code;
    }

    protected void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    protected void setMsg(String msg) {
        this.msg = msg;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    protected void setOut(ByteArrayOutputStream out) {
        this.out = out;
    }

    public Integer getCode() {
        return code;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMsg() {
        return msg;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public ByteArrayOutputStream getOut() {
        return out;
    }
}
