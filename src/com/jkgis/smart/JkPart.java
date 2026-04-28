package com.jkgis.smart;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.Part;

import com.jkgis.smart.util.JkSmartUtil;

public class JkPart implements Part{
    private String content_type ;
    private String name ;
    private long size ;
    private String filename ;
    private Map<String, String> header;
    private File file ;
    private byte[] inputByte ;

    public JkPart(){
        super();
    }

    public JkPart(String type, String name, long size, String filename, Map<String, String> header){
        this.content_type = type;
        this.name = name ;
        this.size = size; 
        this.filename = filename;
        this.header = header ;
    }

    public void setInputStream(byte[] bytes){
        // try {
        //     System.out.println(bytes.length);
        //     String fn = UUID.randomUUID().toString();
        //     String ext = this.filename.substring(this.filename.lastIndexOf("."));
        //     file = new File(JkSmartUtil.APP_HOME+"/temp/"+fn+ext);
        //     file.createNewFile();
        //     OutputStream os = new FileOutputStream(file);
        //     os.write(bytes,0,bytes.length);
        //     os.flush();
        //     os.close();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        try{
            int len = 0;
            int pos = 0;
            if(this.inputByte!=null){
                len = this.inputByte.length;
                pos = len ;
            }
            len += bytes.length;
            byte[] buffer = new byte[len];
            if(this.inputByte!=null){
                System.arraycopy(this.inputByte, 0, buffer, 0, this.inputByte.length);
            }
            System.arraycopy(bytes, 0, buffer, pos, bytes.length);
            this.inputByte = buffer;
        }catch(Throwable t){
            System.out.println("throwable >> "+t.getMessage());
        }
    }

    @Override
    public void delete() throws IOException {
        if(file!=null)file.delete();
    }

    @Override
    public String getContentType() {
        return this.content_type;
    }

    @Override
    public String getHeader(String name) {
        return header.get(name);
    }

    @Override
    public Collection<String> getHeaderNames() {
        Collection<String> ls = new ArrayList<>();
        Iterator<String> iter = header.keySet().iterator();
        while(iter.hasNext()){
            ls.add(iter.next());
        }
        return ls;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        //InputStream in =  new FileInputStream(file);
        return inputByte==null?null: new ByteArrayInputStream(inputByte);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public long getSize() {
        return this.size;
    }

    @Override
    public String getSubmittedFileName() {
        return this.filename;
    }

    @Override
    public void write(String fileName) throws IOException {
        // TODO Auto-generated method stub
        
    }
    
}
