package com.jkgis.smart;

import java.util.Map;

import javax.servlet.http.Part;


public class JkRequest  {
    
    private String method;

    private String httpVer;

    private String url;

    private String path;

    private Map<String, String> headers;

    private Map<String, String> attrs;

    private Map<String, String> params ;

    private Part part ;



    void setMethod(String method) {
        this.method = method;
    }

    void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    void setParams(Map<String, String> params){
        this.params = params ;
    }

    public Map<String, String> getParams(){
        return this.params;
    }

    public Map<String, String> getAttrs() {
        return attrs;
    }

    void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return url;
    }

    void setUrl(String url) {
        this.url = url;
    }

    void setHttpVer(String httpVer) {
        this.httpVer = httpVer;
    }

    public String getHttpVer() {
        return httpVer;
    }

    public String getMethod() {
        return method;
    }

    public String getParameter(String ps){
        return this.params==null?null: this.params.get(ps);
    }

    public String getHeader(String p){
        return this.headers.get(p);
    }

    public void setPart(Part p){
        this.part = p ;
    }

    public Part getPart(){
        return this.part;
    }
}
