package com.jkgis.smart.servlet;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.jkgis.poi.Office2Pdf;
import com.jkgis.python.PyAssitant;
import com.jkgis.python.Xmind;
import com.jkgis.smart.JkRequest;
import com.jkgis.smart.JkResponse;
import com.jkgis.smart.server.Mine;
import com.jkgis.smart.util.JkSmartUtil;
import java.net.URLEncoder;

public class HtmlServlet {
    private String staticRootPath;

    public void doGet(JkRequest request, JkResponse response) {
        // TODO Auto-generated method stub

    }

    public void doPost(JkRequest request, JkResponse response) {
        // TODO Auto-generated method stub

    }

    public String doMake(String path) {
        String result = "";
        String dir = JkSmartUtil.getString("data_home");
        String dir1 = JkSmartUtil.getString("static_home");
        String[] fs = path.split("[?]");// cgi
        String c_path = fs[0].substring(fs[0].indexOf("/"));
        c_path = c_path.substring(0, c_path.length() - 3);
        String[] tmps = fs[1].split("&");
        String filename = "";
        for (String s : tmps) {
            String[] tmp = s.split("=");
            if (tmp[0].equals("url")) {
                filename = tmp[1];
            } else {
                System.out.println(tmp[0] + "=" + tmp[1]);
            }
        }
        String n_path = filename.indexOf("/") > -1 ? filename.substring(filename.lastIndexOf("/") + 1) : filename;// 防止跨文件夹访问，不包含/的文件名

        String src = dir + c_path + "/" + n_path;

        n_path = n_path.substring(0, n_path.indexOf("."));// 新文件夹

        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        String _root = dir + c_path + "/" + n_path;
        _root = _root.replace("//", "/");

        System.out.println(_root);
        File _dir = new File(_root);
        if (_dir.exists()) {
            if (ext.equals("ppt") || ext.equals("pptx")) {
                result = "ppt.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("xmind")) {
                result = "xmind.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("rar")) {
                result = "rar.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("zip")) {
                result = "zip.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("rtf")) {
                result = "rtf.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("wps")) {
                result = "soffice.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("psd")) {
                result = "psd.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("cdr")) {
                result = "cdr.HTM?path=" + c_path + '/' + n_path;
            } else if (ext.equals("ai")) {
                result = "ai.HTM?path=" + c_path + "/" + n_path;
            } else if (ext.equals("vsdx")) {
                result = "vsdx.HTM?path=" + c_path + "/" + n_path;
            } else {
                result = c_path + "/" + n_path + "/index.HTM";
            }
        } else {
            _dir.mkdir();
            String html = "";
            if ("doc".equals(ext)) {
                html = Office2Pdf.doc2Html(src, _root);
                result = c_path + "/" + n_path + "/index.HTM";
                html = html.replaceAll("</style>", "\r\nimg {width:auto; max-width:100%; height:auto;}\r\n</style>");
                dir = dir.replace("\\", "\\\\");
                html = html.replaceAll(dir, "");
            } else if ("xls".equals(ext)) {
                html = Office2Pdf.xls2Html(src, _root);
                result = c_path + "/" + n_path + "/index.HTM";
                dir = dir.replace("\\", "\\\\");
                html = html.replaceAll(dir, "");
            } else if ("ppt".equals(ext)) {
                html = Office2Pdf.ppt2Image(src, _root);
                result = "ppt.HTM?path=" + c_path + "/" + n_path;
            } else if ("pptx".equals(ext)) {
                html = Office2Pdf.pptx2Image(src, _root);
                result = "ppt.HTM?path=" + c_path + "/" + n_path;
            } else if ("xmind".equals(ext)) {
                System.out.println("PYTHON_HOME===" + JkSmartUtil.getString("python_home"));
                html = Xmind.xmind2JSON(src, JkSmartUtil.getString("python_home") + "/xmind.py");
                result = "xmind.HTM?path=" + c_path + "/" + n_path;
            } else if ("rar".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/rar.py");
                result = "rar.HTM?path=" + c_path + "/" + n_path;
            } else if ("zip".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/zip.py");
                result = "zip.HTM?path=" + c_path + "/" + n_path;
            } else if ("rtf".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/rtf.py");
                result = "rtf.HTM?path=" + c_path + "/" + n_path;
            } else if ("wps".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/soffice.py");
                result = "soffice.HTM?path=" + c_path + "/" + n_path;
            } else if ("psd".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/psd.py");
                result = "psd.HTM?path=" + c_path + "/" + n_path;
            } else if ("cdr".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/cdr.py");
                result = "cdr.HTM?path=" + c_path + "/" + n_path;
            } else if ("ai".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/ai.py");
                result = "ai.HTM?path=" + c_path + "/" + n_path;
            } else if ("vsdx".equals(ext)) {
                html = PyAssitant.pyExec(src, JkSmartUtil.getString("python_home") + "/xvsdx.py");
                result = "vsdx.HTM?path=" + c_path + "/" + n_path;
            }
            try {

                System.out.println(html);
                System.out.println(dir + c_path + "/" + n_path +"/index.HTM");
                File file = new File(dir + c_path + "/" + n_path + "/index.HTM");

                FileOutputStream writerStream = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
                writer.write(html);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private String storeAttach(JkRequest request) {
        String result = null;
        try {
            String[] fs = request.getUrl().split("[?]");// upload
            String c_path = fs[0].substring(fs[0].indexOf("/"));// 网站目录
            c_path = c_path.substring(0, c_path.length() - 6);// 去掉upload的目录

            String filename = request.getPart().getSubmittedFileName();
            InputStream in = request.getPart().getInputStream();
            ByteArrayOutputStream swap = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int c = 0;
            while ((c = in.read(buff, 0, 1024)) > 0) {
                swap.write(buff, 0, c);
            }
            byte[] bytes = swap.toByteArray();

            System.out.println("file length: " + bytes.length);

            String fn = UUID.randomUUID().toString();
            String ext = filename.substring(filename.lastIndexOf("."));
            File file = new File(JkSmartUtil.getString("data_home") + c_path + fn + ext);
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(bytes, 0, bytes.length);
            os.flush();
            os.close();
            String md5 = DigestUtils.md5Hex(new FileInputStream(file));
            filename = filename.replaceAll("\"", "＂");
            result = "{\"result\":\"success\",\"file\":{\"name\":\"" + fn + ext + "\",\"size\":" + file.length()
                    + ",\"oname\":\""
                    + filename + "\",\"path\":\"" + c_path + "\",\"md5\":\"" + md5 + "\",\"ext\":\"" + ext.substring(1)
                    + "\"}}";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public void doService(JkRequest request, JkResponse response) throws FileNotFoundException {
        String dir = JkSmartUtil.getString("static_home");
        String path = request.getUrl();
        // System.out.println(">>"+path);

        String[] exts = path.split("[?]");
        String ext = exts[0].substring(exts[0].lastIndexOf(".") + 1);
        String filename = exts[0];// path.substring(path.lastIndexOf("/")+1);

        if (filename.contains("cgi")) {
            filename = doMake(request.getUrl());
            Map<String, String> ms = response.getHeaders();

            String[] tmps = filename.split("[?]");
            if (tmps.length > 1) {
                tmps = tmps[1].split("&");
                filename = filename.split("[?]")[0];
                for (String s : tmps) {
                    String[] tmp = s.split("=");
                    ms.put(tmp[0], tmp[1]);
                }
                response.setHeaders(ms);
            }
            // System.out.println("cgi==="+filename);
            if (filename.indexOf("/")>-1) {
               dir = JkSmartUtil.getString("data_home");
            }
        } else if (filename.contains("upload")) {
            if (request.getMethod().equalsIgnoreCase("get")) {
                throw new FileNotFoundException("403");
            } else if (request.getMethod().equalsIgnoreCase("options")) {
                String result = "OK," + System.currentTimeMillis();
                filename = "ok.HTM";
                File file = new File(dir + filename);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    byte[] bytes = result.getBytes();
                    file.createNewFile();
                    OutputStream os = new FileOutputStream(file);
                    os.write(bytes);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    System.out.println("options method err : " + e.getMessage());
                }
            } else {
                String result = storeAttach(request);
                filename = "uploaded.HTM";
                File file = new File(dir + filename);
                if (file.exists()) {
                    file.delete();
                }
                try {
                    byte[] bytes = result.getBytes("utf-8");
                    file.createNewFile();
                    OutputStream os = new FileOutputStream(file);
                    os.write(bytes, 0, bytes.length);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // System.out.println("path=" + path);
            while(path.indexOf("//")>-1){
                path = path.replaceAll("//", "/");
            }
            // System.out.println("dir=" + dir);
            // System.out.println("filename=" + filename);
            while(filename.indexOf("//")>-1){
                filename = filename.replaceAll("//", "/");
            }
            String[] ps = path.split("/");
            boolean isProc = false;
            if(ps.length==0 && filename.equals("/")) {
                //System.out.println(filename);
                filename = "/index.html"; 
                isProc = true;
            }else{
                if (ps[1].indexOf(".") > 0 || ps[1].indexOf("?") > 0) {
                    isProc = true;
                } else {
                    File root = new File(dir);
                    File[] rs = root.listFiles();
                    for (int i = 0; i < rs.length; i++) {
                        if (rs[i].isDirectory()) {
                            // System.out.println(rs[i].getName());
                            if (rs[i].getName().equals(ps[1])) {
                                isProc = true;
                                break;
                            }
                        }
                    }
                }
            }
            // System.out.println("ps=" + ps.length + " " + ps[0] + "," + ps[1]);

            if (!isProc) {
                dir = JkSmartUtil.getString("data_home");
            }
        }

        System.out.println("??"+dir+filename);
        File fs = new File(dir + filename);
        if (!fs.exists()) {
            throw new FileNotFoundException("404");
        } else {
            System.out.println(fs.getAbsolutePath());
            if (!fs.getPath().startsWith(JkSmartUtil.getString("static_home"))) {
                //throw new FileNotFoundException("500");
            } else if (fs.getPath().contains("WEB-INF")) {
                throw new FileNotFoundException("403");
            }
        }
        try {
            Map<String, String> rs = response.getHeaders();
            RandomAccessFile file = new RandomAccessFile(dir + filename, "r");

            // FileInputStream in = new FileInputStream(dir+filename);

            String mine = Mine.getMine(ext);
            // System.out.println("ext: "+ext);
            // System.out.println(mine);
            if (mine != null) {
                // System.out.println(ext+">>"+mine);
                rs.put("content-type", mine);// +";charset=utf-8");
                // rs.put("Content-Length", ""+file.length());
                if (ext.equals("html")) { // } || ext.equals("js") || ext.equals("css")){
                    rs.put("Content-Disposition", "attachment; filename=\"fake.html\"");
                }
            }

            FileChannel channel = file.getChannel();
            ByteBuffer buff = ByteBuffer.allocate((int) channel.size());
            channel.read(buff);
            buff.flip();
            byte[] html = new byte[buff.limit()];
            buff.get(html);
            file.close();

            // byte[] bytes = new byte[1024];

            // int len;
            // while ((len = in.read(bytes)) > -1) {
            // response.getOut().write(bytes, 0, len);
            // }
            // in.close();
            response.getOut().write(html);
            ;// = buff;
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
            throw new FileNotFoundException("404");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void init(JkRequest request, JkResponse response) {
        // TODO Auto-generated method stub
        staticRootPath = JkSmartUtil.getString("static_home");
    }

}
