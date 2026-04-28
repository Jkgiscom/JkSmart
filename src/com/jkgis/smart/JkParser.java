package com.jkgis.smart;

import java.net.URLDecoder;
import java.util.*;

public class JkParser {

    public static JkRequest parseRequest(byte[] buff) {
        JkRequest req = null;
        try {
            if (buff != null) {
                boolean debug = false;
                String str = new String(buff, "utf-8");
                String[] headers = str.trim().split("\r\n");

                if (headers.length > 0) {
                    req = new JkRequest();
                    String line1 = headers[0];

                    String[] mapinfo = line1.trim().split("\\s+");
                    req.setMethod(mapinfo[0]);
                    req.setUrl(URLDecoder.decode(mapinfo[1], "utf-8"));
                    if (line1.indexOf("dsfasdfasdf.jsp") > -1) {
                        debug = true;
                    }
                    req.setHttpVer(mapinfo[2]);

                    Map<String, String> header = new HashMap<>();
                    int idx = 0;
                    for (int i = 1; i < headers.length; i++) {
                        if (debug) {
                            System.out.println("debug ---> " + headers[i]);
                        }
                        if ("".equals(headers[i].trim())) {
                            idx = i;
                            break;
                        }
                        String[] tmps = headers[i].trim().split(":");
                        header.put(tmps[0], tmps[1]);
                    }

                    req.setHeaders(header);

                    String url = req.getUrl();
                    if (debug) {
                        System.out.println(url);
                    }
                    Map<String, String> params = new HashMap<>();
                    req.setPath(url);
                    if (url != null && !"".equals(url)) {
                        String[] href = url.split("[?]");
                        // System.out.println(href);
                        req.setPath(href[0]);
                        if (href.length > 1) {
                            String[] ps = href[1].split("&");
                            for (String s : ps) {
                                String[] tmp = s.split("=");
                                if (tmp.length > 1)
                                    params.put(tmp[0], tmp[1]);
                            }
                        }
                    }

                    if (idx > 0 && idx < headers.length) {
                        for (int i = idx; i < headers.length; i++) {
                            if ("".equals(headers[i].trim()))
                                continue;
                            String[] tmps = headers[i].trim().split("&");
                            for (String s : tmps) {
                                String[] tmp = s.trim().split("=");
                                if (tmp.length > 1) {
                                    if ("post".equalsIgnoreCase(req.getMethod())) {
                                        tmp[1] = URLDecoder.decode(tmp[1], "utf-8");
                                    }
                                    params.put(tmp[0], tmp[1]);
                                }
                            }
                        }
                    }

                    req.setParams(params);

                }
            }
        } catch (Exception e) {
            System.out.println("parseRequest " + e.getMessage());
        }
        return req;
    }

    public static JkRequest parseRequestMulit(byte[] buff) {
        JkRequest req = null;
        try {
            if (buff != null) {
                boolean debug = false;
                String str = new String(buff, "ascii");
                String str_utf = new String(buff, "utf-8");
                String[] header_utf = str_utf.split("\r\n");
                String[] headers = str.split("\r\n");
                int already = 0;
                if (headers.length > 0) {
                    req = new JkRequest();
                    String line1 = headers[0];
                    already += line1.getBytes().length;
                    String[] mapinfo = line1.trim().split("\\s+");
                    req.setMethod(mapinfo[0]);
                    req.setUrl(URLDecoder.decode(mapinfo[1], "utf-8"));
                    if (line1.indexOf("dsfasdfasdf.jsp") > -1) {
                        debug = true;
                    }
                    req.setHttpVer(mapinfo[2]);

                    Map<String, String> header = new HashMap<>();
                    int idx = 0;
                    for (int i = 1; i < headers.length; i++) {
                        idx = i;
                        already += headers[i].getBytes().length;
                        if (debug) {
                            System.out.println("debug ---> " + headers[i]);
                        }
                        if ("".equals(headers[i].trim())) {
                            break;
                        }
                        String[] tmps = headers[i].trim().split(":");
                        header.put(tmps[0], tmps[1]);
                    }

                    req.setHeaders(header);

                    String url = req.getUrl();
                    if (debug) {
                        System.out.println(url);
                    }
                    Map<String, String> params = new HashMap<>();
                    req.setPath(url);
                    if (url != null && !"".equals(url)) {
                        String[] href = url.split("[?]");
                        // System.out.println(href);
                        req.setPath(href[0]);
                        if (href.length > 1) {
                            String[] ps = href[1].split("&");
                            for (String s : ps) {
                                String[] tmp = s.split("=");
                                if (tmp.length > 1)
                                    params.put(tmp[0], tmp[1]);
                            }
                        }
                    }

                    String type = req.getHeader("Content-Type");
                    System.out.println(type);
                    // if (type.trim().startsWith("multipart/form-data")) {//
                    // .indexOf("------WebKitFormBoundary")>-1){
                    // if (idx > 0 && idx < headers.length) {
                    
                    String os = System.getProperty("os.name").toLowerCase();
                    if (idx > 0 && idx < headers.length){
                        for (int i = idx; i < headers.length; i++) {
                            already += headers[i].getBytes().length;
                            String t = header_utf[i];//.trim();
                            System.out.println(t);
                            if (t.startsWith("Content-Disposition")) {
                                String[] tmps = t.split(";");
                                
                                if (tmps.length >= 3) {
                                    System.out.println(header_utf[i + 1]);
                                    System.out.println(header_utf[i + 2]);

                                    int _already = 0 ;
                                    String _type = header_utf[i + 1];
                                    //already += headers[i + 1].getBytes().length;
                                    //_already += headers[i + 1].getBytes().length;
                                    String _name = tmps[1].split("=")[1];
                                    _name = _name.replaceAll("\"", "");
                                    String _filename = tmps[2].split("=")[1];
                                    _filename = _filename.replaceAll("\"", "");
                                    System.out.println(_filename);
                                    long _size = 0;
                                    JkPart part = new JkPart(_type, _name, _size, _filename, req.getHeaders());
                                    //already += headers[i + 2].getBytes().length;
                                    //_already += headers[i + 2].getBytes().length;
                                    System.out.println(already);

                                    int length = 0 ;
                                    if(os.startsWith("windows")){
                                        //_already += (i + 3) * 2;
                                        //already += _already;
                                        System.out.println("lines is"+ headers.length+", current line "+i);
                                        for(int m=i+3; m<headers.length-1; m++){
                                            length += headers[m].length()+2;
                                            System.out.println("line "+m+">>"+headers[m].length());
                                        }
                                    }else{
                                        //System.out.println("1,2>>"+(headers[i + 1].getBytes().length + headers[i + 2].getBytes().length));
                                        //already += headers.length*2; //headers[i + 1].getBytes().length + headers[i + 2].getBytes().length; //_already ; //(i+1)*2;
                                        System.out.println("lines is"+ headers.length+", current line "+i);
                                        //System.out.println(headers[i+3].length());
                                        //int m = i+3 ;
                                        for(int m=i+3; m<headers.length-1; m++){
                                            length += headers[m].length()+2;
                                            System.out.println("line "+m+">>"+headers[m].length());
                                        }
                                    }

                                    int _len = 0;
                                    int last = 0 ;

                                    if(headers[headers.length-1].indexOf("WebKitFormBoundary")>-1){
                                        last += headers[headers.length-1].getBytes().length+4;
                                    }
                                    System.out.println(headers[headers.length-1]);

                                    if(!os.startsWith("windows")){
                                        System.out.println("last line length = "+headers[headers.length-1].length());
                                        last = headers[headers.length-1].getBytes().length+4;
                                    }
                                    
                                    _len = length-2; //headers[i+3].length(); //buff.length - already - last;
                                    
                                    //last = headers[headers.length-1].length();
                                    already = buff.length - _len - last;

                                    byte[] bytes = new byte[_len];
                                    System.out.println(buff.length);
                                    System.out.println(_len);
                                    System.out.println(already);
                                    System.out.println(last);
                                    System.out.println(buff.length+">>>"+(already+_len+last));
                                    System.arraycopy(buff, already, bytes, 0, _len);

                                    part.setInputStream(bytes);
                                    req.setPart(part);
                                    break;
                                } else {
                                    String _name = tmps[1].split("=")[1];
                                    _name = _name.replaceAll("\"", "");
                                    params.put(_name, header_utf[i + 2]);
                                }
                            }
                        }
                    }
                    req.setParams(params);
                }
            }
        } catch (Exception e) {
            System.out.println("parseRequestMulit异常 " + e.getMessage());
        }
        return req;
    }

    public static JkRequest parseMulitData(JkRequest request, byte[] buff) {
        try {
            boolean debug = false;
            String str = new String(buff, "ascii");
            String str_utf = new String(buff, "utf-8");
            String[] header_utf = str_utf.split("\r\n");
            String[] headers = str.split("\r\n");
            int already = 0;
            System.out.println("数据行数：" + headers.length);
            System.out.println(
                    "last line : " + (headers[headers.length - 1].length() > 100 ? "省略" : headers[headers.length - 1]));
            if (headers.length > 0) {
                boolean isByteData = true;
                for (int i = 0; i < headers.length; i++) {
                    if (headers[i].indexOf("Content-Disposition") > -1) {
                        isByteData = false;
                        break;
                    }
                }
                if (isByteData) {
                    int _len = 0;
                    String last = headers[headers.length-1];
                    if(last.indexOf("WebKitFormBoundary")>0){
                        _len = buff.length - (last.getBytes().length+4);    
                    }

                    byte[] bytes = new byte[_len];
                    System.out.println(_len);
                    System.out.println(already);
                    System.arraycopy(buff, already, bytes, 0, _len);
                    JkPart part = (JkPart) request.getPart();
                    if (request.getPart() == null) {
                        //part = new JkPart(_type, _name, _size, _filename, request.getHeaders());
                        System.out.println("未知错误！");
                    }
                    part.setInputStream(bytes);
                    request.setPart(part);
                } else {
                    for (int i = 0; i < headers.length; i++) {
                        already += headers[i].getBytes().length;
                        String t = header_utf[i].trim();
                        if (t.startsWith("Content-Disposition")) {
                            System.out.println(t);
                            String[] tmps = t.split(";");
                            if (tmps.length >= 3) {
                                String _type = header_utf[i + 1];
                                already += headers[i + 1].getBytes().length;
                                String _name = tmps[1].split("=")[1];
                                _name = _name.replaceAll("\"", "");
                                String _filename = tmps[2].split("=")[1];
                                _filename = _filename.replaceAll("\"", "");
                                long _size = 0;
                                already += headers[i + 2].getBytes().length;
                                already += (i + 3) * 2;
                                int _len = 0;
                                int last = 0 ;

                                if(headers[headers.length-1].indexOf("WebKitFormBoundary")>-1){
                                    last += headers[headers.length-1].getBytes().length+4;
                                }
                                _len = buff.length - already - last;
                                byte[] bytes = new byte[_len];
                                System.out.println(_len);
                                System.out.println(already);
                                System.arraycopy(buff, already, bytes, 0, _len);

                                JkPart part = null;
                                if (request.getPart() == null) {
                                    part = new JkPart(_type, _name, _size, _filename, request.getHeaders());
                                }
                                part.setInputStream(bytes);
                                request.setPart(part);
                            } else {
                                Map<String, String> params = request.getParams();
                                String _name = tmps[1].split("=")[1];
                                _name = _name.replaceAll("\"", "");
                                params.put(_name, header_utf[i + 2]);
                                request.setParams(params);
                            }
                        }
                    }
                }
            }
            return request;
        } catch (Exception e) {
            System.out.println("parseMulitData " + e.getMessage());
            return null;
        }
    }

    public static byte[] responseHeader(JkResponse response) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(response.getProtocol() + " " + response.getCode() + " " + response.getMsg());
            sb.append("\r\n");
            Map<String, String> header = response.getHeaders();
            header.entrySet().forEach(entry -> {
                sb.append(entry.getKey());
                sb.append(":");
                sb.append(entry.getValue());
                sb.append("\r\n");
                // System.out.println(entry.getKey()+">>"+entry.getValue());
            });
            // long len1 =
            // ("Content-Length:\r\n"+len+sb.toString()).getBytes("utf-8").length;
            // sb.append("Content-Length:"+(len+len1)+"\r\n");
            sb.append("\r\n");
            String str = sb.toString();
            byte[] bytes = null;

            bytes = str.getBytes("utf-8");
            return bytes;
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("getOutputStream " + e.getMessage());
            return null;
        }

    }
}
