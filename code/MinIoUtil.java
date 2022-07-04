package com.znv.manage.minio;

import com.alibaba.cloud.commons.io.IOUtils;
import io.minio.MinioClient;
import io.minio.ObjectStat;
import io.minio.PutObjectOptions;
import io.minio.messages.Bucket;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

@Slf4j
@Component
public class MinIoUtil {

    @Autowired
    MinioConfig minioConfig;
    private static MinioClient minioClient;

    /**
     * 初始化minio配置
     *
     * @param :
     * @return: void
     * @date :
     */

    @PostConstruct
    public void init() {
        try {
            minioClient = new MinioClient(minioConfig.getEndpoint(), minioConfig.getPort(), minioConfig.getAccessKey(), minioConfig.getSecretKey(), false);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("init error: 【{}】", e.fillInStackTrace());
        }
    }

    /**
     * 判断 bucket是否存在
     *
     * @param bucketName: 桶名
     * @return: boolean
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static boolean bucketExists(String bucketName) {
        return minioClient.bucketExists(bucketName);
    }

    /**
     * 创建 bucket
     *
     * @param bucketName: 桶名
     * @return: void
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static void createBucket(String bucketName) {
        boolean isExist = minioClient.bucketExists(bucketName);
        if (!isExist) {
            minioClient.makeBucket(bucketName);
        }
    }

    /**
     * 获取全部bucket
     *
     * @param :
     * @return: java.util.List<io.minio.messages.Bucket>
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static List<Bucket> getAllBuckets() {
        return minioClient.listBuckets();
    }

    /**
     * 文件上传
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     * @param filePath:   文件路径
     * @return: void
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static void upload(String bucketName, String fileName, String filePath) {
        minioClient.putObject(bucketName, fileName, filePath, null);
    }

    /**
     * 文件上传
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     * @param stream:     文件流
     * @return: java.lang.String : 文件url地址
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, String fileName, InputStream stream) {
        minioClient.putObject(bucketName, fileName, stream, new PutObjectOptions(stream.available(), -1));
        return getFileUrl(bucketName, fileName);
    }

    /**
     * 文件上传
     *
     * @param bucketName: 桶名
     * @param file:       文件
     * @return: java.lang.String : 文件url地址
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static String upload(String bucketName, MultipartFile file) {
        final InputStream is = file.getInputStream();
        final String fileName = file.getOriginalFilename();
        minioClient.putObject(bucketName, fileName, is, new PutObjectOptions(is.available(), -1));
        is.close();
        return getFileUrl(bucketName, fileName);
    }

    /**
     * 删除文件
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     * @return: void
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static void deleteFile(String bucketName, String fileName) {
        minioClient.removeObject(bucketName, fileName);
    }

    /**
     * 获取minio文件的下载地址
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     * @return: java.lang.String
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static String getFileUrl(String bucketName, String fileName) {
        return minioClient.presignedGetObject(bucketName, fileName);
    }

    /**
     * 下载文件
     *
     * @param bucketName: 桶名
     * @param fileName:   文件名
     * @param
     * @return: void
     * @date :
     */
    @SneakyThrows(Exception.class)
    public static InputStream download(String bucketName, String fileName) {
        // 获取对象的元数据
        return minioClient.getObject(bucketName, fileName);
    }
}
