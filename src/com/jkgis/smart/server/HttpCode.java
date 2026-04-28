package com.jkgis.smart.server;

public enum HttpCode {
    STATUS_200(200, "搞定"),
    STATUS_400(400, "错误的请求"),
    STATUS_403(403, "闲人免进"),
    STATUS_404(404, "页面挂了"),

    /**
     * 永久重定向
     */
    STATUS_301(301, "永久转移"),

    /**
     * 临时重定向
     */
    STATUS_302(302, "发现好像又没发现"),
    STATUS_500(500, "程序错误"),
    STATUS_503(503, "程序不可用");
    
    private Integer code ;
    private String msg ;

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    HttpCode(Integer code, String msg){
        this.code = code;
        this.msg = msg ;
    }

    
}
