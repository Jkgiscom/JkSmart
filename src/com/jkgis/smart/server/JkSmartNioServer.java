package com.jkgis.smart.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.Part;

import java.util.*;

import com.jkgis.smart.JkParser;
import com.jkgis.smart.JkPart;
import com.jkgis.smart.JkRequest;
import com.jkgis.smart.JkRequestWork;
import com.jkgis.smart.JkSmartServer;
import com.jkgis.smart.servlet.HtmlServlet;
import com.jkgis.smart.util.JkSmartUtil;

public class JkSmartNioServer implements Runnable {

    private Thread serverThread;
    private boolean running = false;

    private static volatile JkSmartNioServer server;

    private ServerSocketChannel channel;

    private static final ExecutorService rq = Executors.newCachedThreadPool();

    private ByteBuffer reaBuffer = ByteBuffer.allocate(8192);

    private HtmlServlet servlet;

    private static Map<String, JkRequest> reqs ;

    public JkSmartNioServer() {
        reqs = new HashMap<>();
        this.servlet = new HtmlServlet();
    }

    public static JkSmartNioServer newInstance() {
        if (server == null) {
            server = new JkSmartNioServer();
        }
        return server;
    }

    public void server() {
        if (running) {
            System.out.println("JkSmartNio is running");
            return;
        }
        serverThread = new Thread(this);
        serverThread.start();
        running = true;
    }

    public void close() {
    }

    public void run() {
        try {
            channel = ServerSocketChannel.open();
            channel.socket().bind(new InetSocketAddress(JkSmartUtil.getIntValue("port")));
            Selector select = Selector.open();
            channel.configureBlocking(false);
            
            channel.register(select, SelectionKey.OP_ACCEPT);
            while (true) {
                try {
                    select.select();
                    Iterator<SelectionKey> keys = select.selectedKeys().iterator();
                    SelectionKey key = null;
                    while (keys.hasNext()) {
                        key = keys.next();
                        keys.remove();
                        if (!key.isValid())
                            continue;
                        if (key.isAcceptable()) {
                            accept(key);
                        }
                        if (key.isReadable()) {
                            read(key);
                        }
                        if (key.isWritable()) {
                            wirte(key);
                        }
                    }
                } catch (Exception ex) {
                    System.out.println("run err "+ex.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void accept(SelectionKey key) {
        try {
            ServerSocketChannel schnannel = (ServerSocketChannel) key.channel();
            SocketChannel socket = schnannel.accept();
            if(schnannel!=null){
                socket.configureBlocking(false);
                socket.register(key.selector(), SelectionKey.OP_READ);
                socket.socket().setSoTimeout(5*60*1000);
                socket.socket().setSendBufferSize(1024*1024*300);
                //socket.socket().setKeepAlive(false);
                //socket.socket().setReceiveBufferSize(1024*1024*200);
                //socket.socket().setKeepAlive(true);
                //socket.socket().setOOBInline(true);
            }
        } catch (Exception e) {
            System.out.println("accept exception " + e.getMessage());
        }
    }

    private JkRequest read(SelectionKey key) {
        JkRequest request = null;
        try {
            SocketChannel socket = (SocketChannel) key.channel();
            //socket.socket().setSendBufferSize(1024*1024*30);
            //socket.socket().setReceiveBufferSize(1024*1024*30);
            int num = 0 ;// socket.read(reaBuffer);
            if(num==-1){
                socket.close();
                key.cancel();
                return null ;
            }
            List<byte[]> ls = new ArrayList<>();
            int totalCount = 0 ;
            StringBuilder sb = new StringBuilder();
            boolean isMulit = false;
            boolean notTerminated = true;
            String uid = ""+socket.hashCode();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSSS");
            System.out.println("----------------"+sdf.format(new Date())+"-------socket："+uid);
            int k = 1 ;
            while ((num=socket.read(reaBuffer))>0) {
                reaBuffer.flip();
                byte[] buffer = new byte[reaBuffer.limit()];
                reaBuffer.get(buffer);

                String bufString = new String(buffer, "ascii");
                sb.append(bufString);
                if(bufString.indexOf("Content-Length")>-1){
                    String tmp = bufString.substring(bufString.indexOf("Content-Length"));
                    System.out.println(tmp.substring(0, tmp.indexOf("\r\n")));
                }
                if(!isMulit && bufString.indexOf("multipart/form-data")>-1){
                    isMulit = true;
                }
                ls.add(buffer);
                totalCount += buffer.length;

                if(num==-1){
                    socket.close();
                    key.cancel();
                    return null;
                }
                reaBuffer.clear();
                System.out.println("第"+(k++)+"次");
                if(isMulit){
                    Thread.sleep(JkSmartUtil.getIntValue("WAIT_FOR"));
                }
            }

            if(totalCount==0){
                socket.close();
                key.cancel();
                return null;
            }
            byte[] buff = new byte[totalCount];
            int count = 0 ;
            for(int i=0; i<ls.size(); i++){
                System.arraycopy(ls.get(i), 0, buff, count, ls.get(i).length);
                count += ls.get(i).length;
            }

            System.out.println("totol len "+totalCount);
            String[] lines = sb.toString().split("\r\n");
            String first = lines[0];
            String last = lines[lines.length-1];
            if(last.indexOf("WebKitFormBoundary")>0){
                System.out.println(last);
                notTerminated = false ;
            }
            System.out.println("line1 : "+(first.length()>255?first.substring(0, 63):first));
            //JkRequest req = null;
            System.out.println("isBound: "+socket.socket().isBound());
            boolean skip = false;
            if(first.startsWith("OPTIONS")){
                isMulit = false;
            }else if(first.indexOf("HTTP")<0){// && line.indexOf("WebKitFormBoundary")>0){
                isMulit = true;
                request = reqs.get(uid);
                if(first.length()>100 && notTerminated){
                    System.out.println("出现没有任何身份信息的不明数据");
                    //JkPart part =  (JkPart) request.getPart();
                    //part.setInputStream(buff);
                    //request.setPart(part);
                    //reqs.put(uid, request);
                    skip = true;
                }else{
                    System.out.println(last.length()>255?last.substring(0,127):last);
                    System.out.println("request>>"+request);
                    //String bound = first.substring(first.indexOf("WebKitFormBoundary")+"WebKitFormBoundary".length());
                    //System.out.println(">>>"+bound);
                }
            }

            if(isMulit){
                System.out.println("处理附件请求");
                if(request==null){
                    System.out.println("处理附件请求，第一部分");
                    request = JkParser.parseRequestMulit(buff);
                    //rq.execute(new JkRequestWork(request, key, servlet));
                    reqs.put(uid, request);
                }else{
                    if(!skip){
                        System.out.println("处理附件请求，数据部分");
                        request = JkParser.parseMulitData(request, buff);
                        reqs.put(uid, request);
                    }else{
                        System.out.println("略过");
                        //return null;
                    }
                }
                if(!notTerminated){
                    if(true){
                        storeAttach(request);
                    }
                    rq.execute(new JkRequestWork(request, key, servlet));
                }
            }else{
                System.out.println("处理请求");
                request = JkParser.parseRequest(buff);
                System.out.println(request.getPath());
                reqs.put(uid, request);
                rq.execute(new JkRequestWork(request, key, servlet));
            }
        } catch (Exception e) {
            System.out.println("read exception " + e.getMessage());
        }
        return request;
    }

    public static void put(String key, JkRequest request){
        reqs.put(key, request);
    }

    private void wirte(SelectionKey key) throws IOException {
        ByteBuffer buff = (ByteBuffer) key.attachment();
        if (buff == null || !buff.hasRemaining()) {
            return;
        }
        
        SocketChannel channel = (SocketChannel) key.channel();
        //channel.write(buff);
        channel.socket().setSoTimeout(180*1000);
        String os = System.getProperty("os.name").toLowerCase();
        if(os.startsWith("windows")){
            channel.write(buff);
        }else{
            byte[] bytes = new byte[buff.limit()];
            buff.get(bytes);

            List<ByteBuffer> ls = new ArrayList<>();
            int pos = 0 ;
            while(pos<bytes.length){
                int size = 1024*JkSmartUtil.getIntValue("MAX_SIZE") ;
                int len = bytes.length-pos<size?bytes.length-pos:size;
                byte[] b = new byte[len];
                System.arraycopy(bytes, pos, b, 0, len);
                ByteBuffer bf = ByteBuffer.allocate(len);
                bf.put(b);
                
                ls.add(bf);
                pos = pos+len;
            }
            ByteBuffer[] bs = new ByteBuffer[ls.size()];
            for(int i=0; i<ls.size(); i++){
                bs[i] = ls.get(i);
            }
            if(bs.length==1){
                bs[0].flip();
                channel.write(bs[0]);
            }else{
                System.out.println(bs.length);
                int i = 0 ;
                while(i<bs.length){
                    try{
                        bs[i].flip();
                        int ret = channel.write(bs[i]);
                        
                        Thread.sleep(JkSmartUtil.getIntValue("DELAY"));
                        if(ret==0){
                            System.out.println("没有写入数据，可能是网络问题");
                            //ret = channel.write(bs[i]);
                        }
                        
                        // int k = 1;
                        // while(ret==0){
                        //     System.out.println("第"+(k++)+"次>>>"+ret);
                        //     Thread.sleep(50);
                        //     ret = channel.write(bs[i]);
                        //     System.out.println("第"+(k++)+"次>>>"+ret);
                        //     if(k>5){
                        //         System.out.println("强制退出！");
                        //         break;
                        //     }
                        // }
                        i++;

                    }catch(Exception e){}
                }
            }
        }
        if (!buff.hasRemaining()) {
            key.interestOps(SelectionKey.OP_READ);
            buff.clear();
        }
        channel.close();
    }

    private void storeAttach(JkRequest request){
        try {
            String filename = request.getPart().getSubmittedFileName();
            InputStream in = request.getPart().getInputStream();
            ByteArrayOutputStream swap = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int c = 0 ;
            while ((c=in.read(buff,0,1024))>0) {
                swap.write(buff, 0, c);
            }
            byte[] bytes = swap.toByteArray();

            System.out.println("file length: "+bytes.length);

            String fn = UUID.randomUUID().toString();
            String ext = filename.substring(filename.lastIndexOf(".")); 
            File file = new File(JkSmartUtil.APP_HOME+"/temp/"+fn+ext);
            file.createNewFile();
            OutputStream os = new FileOutputStream(file);
            os.write(bytes,0,bytes.length);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
