package com.jkgis.poi;

import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.sl.usermodel.TextRun;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFTextRun;
import org.apache.poi.xslf.usermodel.XSLFTextShape;
import org.omg.CORBA.SystemException;
import org.w3c.dom.Document;

import org.apache.poi.hwpf.usermodel.Picture;

import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlide;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.hslf.usermodel.HSLFTextShape;

import javax.xml.transform.*;

public class Office2Pdf {

    public static String xls2Html(String src, String dest) {
        try {
            String path = new File(dest).getPath();
            InputStream input = new FileInputStream(src);

            HSSFWorkbook excelBook = new HSSFWorkbook(input);
            ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            excelToHtmlConverter.processWorkbook(excelBook);
            List pics = excelBook.getAllPictures();
            if (pics != null) {
                for (int i = 0; i < pics.size(); i++) {
                    Picture pic = (Picture) pics.get(i);
                    try {
                        pic.writeImageContent(new FileOutputStream(path + pic.suggestFullFileName()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
            Document htmlDocument = excelToHtmlConverter.getDocument();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            DOMSource domSource = new DOMSource(htmlDocument);
            StreamResult streamResult = new StreamResult(outStream);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
            outStream.close();

            String content = new String(outStream.toByteArray(), "utf-8");

            // FileUtils.writeStringToFile(new File(path, "exportExcel.html"), content,
            // "utf-8");
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String doc2Html(String docPath, String imageDir) {
        String content = null;
        ByteArrayOutputStream baos = null;
        try {
            HWPFDocument wordDocument = new HWPFDocument(new FileInputStream(docPath));
            WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
            wordToHtmlConverter.setPicturesManager(new PicturesManager() {
                @Override
                public String savePicture(byte[] content, PictureType pictureType, String suggestedName,
                        float widthInches, float heightInches) {
                    File file = new File(imageDir + suggestedName);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        fos.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return imageDir + suggestedName;
                }
            });
            wordToHtmlConverter.processDocument(wordDocument);
            Document htmlDocument = wordToHtmlConverter.getDocument();
            DOMSource domSource = new DOMSource(htmlDocument);
            baos = new ByteArrayOutputStream();
            StreamResult streamResult = new StreamResult(baos);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
            serializer.setOutputProperty(OutputKeys.INDENT, "yes");
            serializer.setOutputProperty(OutputKeys.METHOD, "html");
            serializer.transform(domSource, streamResult);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    content = new String(baos.toByteArray(), "utf-8");
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return content;
    }

    public static String ppt2Image(String src, String dest) {
        String str = "";
        try {
            FileInputStream is = new FileInputStream(src);
            HSLFSlideShow ppt = new HSLFSlideShow(is);
            is.close();
            Dimension pgsize = ppt.getPageSize();
            List<HSLFSlide> slide = ppt.getSlides();
            for (int i = 0; i < slide.size(); i++) {
                System.out.print("第" + i + "页。");
                List<HSLFShape> shapes = slide.get(i).getShapes();
                for (HSLFShape shape : shapes) {
                    if (shape instanceof HSLFTextShape) {
                        HSLFTextShape text = (HSLFTextShape) shape;
                        for (HSLFTextParagraph txt : text.getTextParagraphs()) {
                            for (TextRun tr : txt.getTextRuns()) {
                                tr.setFontFamily("宋体");
                            }
                        }
                    }
                }
                BufferedImage img = new BufferedImage(pgsize.width * 2, pgsize.height * 2, BufferedImage.TYPE_INT_RGB);

                Graphics2D graphics = img.createGraphics();
                graphics.setPaint(Color.WHITE);
                graphics.setFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 12));
                graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width * 2, pgsize.height * 2));
                graphics.scale(2, 2);
                slide.get(i).draw(graphics);
                graphics.dispose();

                // 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径
                String fs = "img_" + (i + 1) + ".png";
                FileOutputStream out = new FileOutputStream(dest + "/" + fs);
                javax.imageio.ImageIO.write(img, "png", out);
                out.close();
                str += ",\"" + fs + "\"";
            }
            str = "{\"rec\":" + slide.size() + ", \"files\":[" + str.substring(1) + "]}";
            ppt.close();
            System.out.println("success!!");
        } catch (FileNotFoundException e) {
            System.out.println(e);
            // System.out.println("Can't find the image!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String pptx2Image(String src, String dest) {
        String str = "";
        try {
            XMLSlideShow slideShow = new XMLSlideShow(new FileInputStream(src));
            Dimension dimension = slideShow.getPageSize();
            List<XSLFSlide> slideList = slideShow.getSlides();
            for (int i = 0, row = slideList.size(); i < row; i++) {
                System.out.print("第" + i + "页。");
                XSLFSlide slide = slideList.get(i);
                // 设置字体, 解决中文乱码

                for (XSLFShape shape : slide.getShapes()) {
                    if (shape instanceof XSLFTextShape) {
                        XSLFTextShape textShape = (XSLFTextShape) shape;
                        for (XSLFTextParagraph textParagraph : textShape.getTextParagraphs()) {
                            for (XSLFTextRun textRun : textParagraph.getTextRuns()) {
                                textRun.setFontFamily("宋体");
                            }
                        }
                    }
                }

                BufferedImage bufferedImage = new BufferedImage((int) dimension.getWidth() * 2,
                        (int) dimension.getHeight() * 2,
                        BufferedImage.TYPE_INT_RGB);
                Graphics2D graphics2d = bufferedImage.createGraphics();
                graphics2d.setPaint(Color.white);
                graphics2d.scale(2, 2);
                graphics2d.setFont(new java.awt.Font("宋体", java.awt.Font.PLAIN, 12));

                slide.draw(graphics2d);

                graphics2d.dispose();

                // Image image = Image.getInstance(bufferedImage, null);
                // image.scalePercent(50f);
                // 这里设置图片的存放路径和图片的格式(jpeg,png,bmp等等),注意生成文件路径
                String fs = "img_" + (i + 1) + ".png";
                FileOutputStream out = new FileOutputStream(dest + "/" + fs);
                javax.imageio.ImageIO.write(bufferedImage, "png", out);
                out.close();
                str += ",\"" + fs + "\"";

            }
            str = "{\"rec\":" + slideList.size() + ", \"files\":[" + str.substring(1) + "]}";
            slideShow.close();
            System.out.println("pptx convert success!");
        } catch (Throwable t) {
            System.out.println("throwable " + t.getMessage());
            // e.printStackTrace();
            if (!"".equals(str)) {
                str = "{\"rec\":999, \"files\":[" + str.substring(1) + "]}";
            }
        }
        return str;
    }

    public static void main(String[] args) {
        // C:\Users\kylin\Documents
        File file = new File("c:\\jksoft_jsp\\ccc\\index.html");
        File dir = new File(file.getParent());
        if (!dir.exists()) {
            dir.mkdir();
        }
        if (file.exists()) {
            file.delete();
        }
        // String str = doc2Html("C:\\Users\\kylin\\Documents\\报价表0610-定稿0610(定稿).doc",
        // "c:\\jksoft_jsp\\aaa\\");
        // String str =
        // xls2Html("C:\\Users\\kylin\\Documents\\衢州\\水亭门信用商圈评分汇总表20201116.xls",
        // "c:\\jksoft_jsp\\bbb\\");
        String str = ppt2Image("d:\\红岭云徐总提供资料（全集）\\任务等系统核心功能描述\\宜春市智慧党建初探（正式稿5.18）.ppt", "c:\\jksoft_jsp\\ccc\\");
        // String str = null;
        // str = pptx2Image("C:\\imgs\\docs\\f84bc522-4898-4f01-a356-f8c155ff1033.pptx",
        // "c:\\jksoft_jsp\\ddd\\");
        System.out.println(str);
        if (str != null)
            try {
                FileOutputStream writerStream = new FileOutputStream(file);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(writerStream, "UTF-8"));
                writer.write(str);
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
