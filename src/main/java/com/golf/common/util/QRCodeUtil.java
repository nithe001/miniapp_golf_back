/*
 * Created on 2005-7-13
 * 
 * 时间工具类.
 */
package com.golf.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;


/**
 * 二维码工具
 *
 * @author peihong
 */
public class QRCodeUtil {

    private static int width = 300;              //二维码宽度
    private static int height = 300;             //二维码高度
    private static int onColor = 0xFF000000;     //前景色
    private static int offColor = 0xFFFFFFFF;    //背景色
    private static int margin = 1;               //白边大小，取值范围0~4
    private static ErrorCorrectionLevel level = ErrorCorrectionLevel.L;  //二维码容错率


    /**
     * 生成二维码（默认大小300X300）
     *
     * @param txt     //二维码内容
     * @param imgPath //二维码保存物理路径
     * @param imgName //二维码文件名称
     */
    public static void createQRImage(String txt, String imgPath, String imgName) {
        String suffix = imgName.substring(imgName.indexOf(".") + 1, imgName.length());
        createQRImage(txt, imgPath, imgName, suffix, width, height);
    }

    /**
     * 生成二维码
     *
     * @param txt     //二维码内容
     * @param imgPath //二维码保存物理路径
     * @param imgName //二维码文件名称
     * @param width   //宽度
     * @param height  //高度
     */
    public static void createQRImage(String txt, String imgPath, String imgName, int width, int height) {
        String suffix = imgName.substring(imgName.indexOf(".") + 1, imgName.length());
        createQRImage(txt, imgPath, imgName, suffix, width, height);
    }

    /**
     * 生成二维码（默认大小300X300）
     *
     * @param txt     //二维码内容
     * @param imgPath //二维码保存物理路径
     * @param imgName //二维码文件名称
     * @param suffix  //图片后缀名
     */
    public static void createQRImage(String txt, String imgPath, String imgName, String suffix) {
        createQRImage(txt, imgPath, imgName, suffix, width, height);
    }

    /**
     * 生成二维码
     *
     * @param txt     //二维码内容
     * @param imgPath //二维码保存物理路径
     * @param imgName //二维码文件名称
     * @param suffix  //图片后缀名
     * @param width   //宽度
     * @param height  //高度
     */
    public static void createQRImage(String txt, String imgPath, String imgName, String suffix, int width, int height) {

        File filePath = new File(imgPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        File imageFile = new File(imgPath, imgName);
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        // 指定纠错等级
        hints.put(EncodeHintType.ERROR_CORRECTION, level);
        // 指定编码格式
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.MARGIN, margin);   //设置白边
        try {
            MatrixToImageConfig config = new MatrixToImageConfig(onColor, offColor);
            BitMatrix bitMatrix = new MultiFormatWriter().encode(txt, BarcodeFormat.QR_CODE, width, height, hints);
//          bitMatrix = deleteWhite(bitMatrix);
            MatrixToImageWriter.writeToPath(bitMatrix, suffix, imageFile.toPath(), config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成带logo的二维码图片
     *
     * @param txt      //二维码内容
     * @param logoPath //logo绝对物理路径
     * @param imgPath  //二维码保存绝对物理路径
     * @param imgName  //二维码文件名称
     * @throws Exception
     */
    public static void createQRImageWithLogo(String txt, String logoPath, String imgPath, String imgName) throws Exception {
        String suffix = imgName.substring(imgName.indexOf(".") + 1, imgName.length());
        createQRImageWithLogo(txt, logoPath, imgPath, imgName, suffix);
    }

    /**
     * 生成带logo的二维码图片
     *
     * @param txt      //二维码内容
     * @param logoPath //logo绝对物理路径
     * @param imgPath  //二维码保存绝对物理路径
     * @param imgName  //二维码文件名称
     * @param width   //宽度
     * @param height  //高度
     * @throws Exception
     */
    public static void createQRImageWithLogo(String txt, String logoPath, String imgPath, String imgName, int width, int height) throws Exception {
        String suffix = imgName.substring(imgName.indexOf(".") + 1, imgName.length());
        createQRImageWithLogo(txt, logoPath, imgPath, imgName, suffix, width, height);
    }

    /**
     * 生成带logo的二维码图片
     *
     * @param txt      //二维码内容
     * @param logoPath //logo绝对物理路径
     * @param imgPath  //二维码保存绝对物理路径
     * @param imgName  //二维码文件名称
     * @param suffix   //图片后缀名
     * @throws Exception
     */
    public static void createQRImageWithLogo(String txt, String logoPath, String imgPath, String imgName, String suffix) throws Exception {
        createQRImageWithLogo(txt, logoPath, imgPath, imgName, suffix, width, height);
    }

    /**
     * 生成带logo的二维码图片
     *
     * @param txt      //二维码内容
     * @param logoPath //logo绝对物理路径
     * @param imgPath  //二维码保存绝对物理路径
     * @param imgName  //二维码文件名称
     * @param suffix   //图片后缀名
     * @param width   //宽度
     * @param height  //高度
     * @throws Exception
     */
    public static void createQRImageWithLogo(String txt, String logoPath, String imgPath, String imgName, String suffix, int width, int height) throws Exception {

        File filePath = new File(imgPath);
        if (!filePath.exists()) {
            filePath.mkdirs();
        }

        if (imgPath.endsWith("/")) {
            imgPath += imgName;
        } else {
            imgPath += "/" + imgName;
        }

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, margin);  //设置白边
        BitMatrix bitMatrix = new MultiFormatWriter().encode(txt, BarcodeFormat.QR_CODE, width, height, hints);
        File qrcodeFile = new File(imgPath);
        writeToFile(bitMatrix, suffix, qrcodeFile, logoPath);
    }

    /**
     * 生成二维码
     *
     * @param txt    内容
     * @param path   生成地址
     * @param top1   二维码顶部显示文字内容1
     * @param top2   二维码顶部显示文字内容1
     * @param bottom 二维码底部显示文字内容
     * @return
     */
    public static boolean createPng(String txt, String path, String top1, String top2, String bottom) {
        Hashtable<EncodeHintType, String> hints = new Hashtable<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix matrix;
        try {
            matrix = new MultiFormatWriter().encode(txt,
                    BarcodeFormat.QR_CODE, 500, 800, hints);
        } catch (WriterException e) {
            e.printStackTrace();
            return false;
        }
        try {
//            MatrixToImageWriter.writeToStream(matrix, "png", new FileOutputStream(new File(path)));
            BufferedImage bi = MatrixToImageWriter.toBufferedImage(matrix);
            if (StringUtils.isNotEmpty(top1)) {
                Graphics g = bi.getGraphics();
                g.setColor(Color.black);
                Font f = new Font("黑体", Font.PLAIN, 24);
                g.setFont(f);
                g.drawString(top1, 250 - top1.length() * 12, 140);
                g.dispose();
            }

            if (StringUtils.isNotEmpty(top2)) {
                if (top2.length() <= 20) {
                    Graphics g = bi.getGraphics();
                    g.setColor(Color.black);
                    Font f = new Font("黑体", Font.BOLD, 24);
                    g.setFont(f);
                    g.drawString(top2, 250 - top2.length() * 12, 180);
                    g.dispose();
                } else {
                    int length = top2.length();
                    int px = Integer.parseInt(500 / length + "") - 2;

                    Graphics g = bi.getGraphics();
                    g.setColor(Color.black);
                    Font f = new Font("黑体", Font.BOLD, px);
                    g.setFont(f);
                    g.drawString(top2, 250 - top2.length() * (px / 2), 180);
                    g.dispose();
                }
            }
            if (StringUtils.isNotEmpty(bottom)) {
                Graphics g = bi.getGraphics();
                g.setColor(Color.black);
                Font f = new Font("黑体", Font.BOLD, 24);
                g.setFont(f);
                g.drawString(bottom, 250 - bottom.length() * 12, 640);
                g.dispose();
            }

            ImageIO.write(bi, "png", new File(path));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param matrix   二维码矩阵相关
     * @param format   二维码图片格式
     * @param file     二维码图片文件
     * @param logoPath logo路径
     * @throws IOException
     */
    public static void writeToFile(BitMatrix matrix, String format, File file, String logoPath) throws IOException {
        BufferedImage image = toBufferedImage(matrix);
        Graphics2D gs = image.createGraphics();

        int ratioWidth = image.getWidth() * 2 / 10;
        int ratioHeight = image.getHeight() * 2 / 10;
        //载入logo
        Image img = ImageIO.read(new File(logoPath));
        int logoWidth = img.getWidth(null) > ratioWidth ? ratioWidth : img.getWidth(null);
        int logoHeight = img.getHeight(null) > ratioHeight ? ratioHeight : img.getHeight(null);

        int x = (image.getWidth() - logoWidth) / 2;
        int y = (image.getHeight() - logoHeight) / 2;

        gs.drawImage(img, x, y, logoWidth, logoHeight, null);
        gs.setColor(Color.black);
        gs.setBackground(Color.WHITE);
        gs.dispose();
        img.flush();
        if (!ImageIO.write(image, format, file)) {
            throw new IOException("Could not write an image of format " + format + " to " + file);
        }
    }

    public static BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor);
            }
        }
        return image;
    }


    public static void main(String[] args) throws Exception {
//        createPng("test", "D:/erweima.png", "top1", "top2", "bottom");
//        createQRImage("测试一下hello哈哈", "D:/", "erweima.png");
        createQRImageWithLogo("测试一下hello哈哈", "C:\\zone\\work\\workspace\\2017\\wc_hp\\target\\wc_hp\\up\\headimg\\olmZ8uJxPElPvL2nnu6qj04n8HbU.jpeg", "D:/", "erweima.png");
    }
}