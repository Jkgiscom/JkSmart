package com.jkgis.python;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jkgis.util.DataUtils;

public class Xmind {
    
    public static String xmind2JSON(String src, String pyString){
        String[] params = new String[1];
        
        String pathWithExt = pyString;// + "/xmind.py" ;

        try {
            String jsonString = "{\"src\":\""+src+"\"}";
            params[0] =  DataUtils.bytesToHexString(jsonString.getBytes("utf-8"));
            String reString = executePython(pathWithExt, params);
            //byte[] content = reString.getBytes("UTF-8");
            return reString;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null ;
    }

        private static String executePython(String path, String[] params) {
        try {
            // Build command: python scriptPath param1 param2 ...
            List<String> command = new ArrayList<>();
            command.add("python3");///
            command.add(path);
            if (params != null) {
                command.addAll(Arrays.asList(params));
            }
            System.out.println("command==");
            System.out.println(command);
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            int exitCode = process.waitFor();
            String output = readStream(process.getInputStream());
            System.out.println("Python Exit Code: " + exitCode);
            // System.out.println("Python Output: " + output);
            System.out.println("Python Error: " + readStream(process.getErrorStream()));
            return output;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e.getMessage();
        }

    }

    private static String readStream(java.io.InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        byte[] buf = new byte[1024];
        int length;
        while ((length = is.read(buf)) != -1) {
            sb.append(new String(buf, 0, length));
        }
        return sb.toString();
    }
}
