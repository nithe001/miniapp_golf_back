package com.kingyee.common.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * HttpUtil工具类
 * Created by ph on 2017/5/9.
 */
public class HttpUtil {

    /**
     * 从网络上下载图片
     * @param url 下载url
     * @param dirPath 文件保存路径
     * @param fileName 要保存的文件名
     */
    public static void downloadPicture(String url, String dirPath, String fileName) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpget = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();
        httpget.setConfig(requestConfig);

        httpget.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.1 (KHTML, like Gecko) Chrome/21.0.1180.79 Safari/537.1");
        httpget.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        CloseableHttpResponse resp = null;
        try {
            resp = httpclient.execute(httpget);
            if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
                HttpEntity entity = resp.getEntity();
                InputStream in = entity.getContent();
                savePicToDisk(in, dirPath, fileName);
            }
        } finally {
            resp.close();
            httpclient.close();
        }
    }

    /**
     * 将图片写到 硬盘指定目录下
     *
     * @param in 文件输入流
     * @param dirPath 文件保存路径
     * @param fileName 要保存的文件名
     */
    private static void savePicToDisk(InputStream in, String dirPath, String fileName) throws IOException {
        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            //文件真实路径
            if(!dirPath.endsWith(File.separator)){
                dirPath = dirPath + File.separator;
            }
            String realPath = dirPath.concat(fileName);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
            fos.close();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
