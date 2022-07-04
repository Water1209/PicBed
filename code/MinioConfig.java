package com.znv.manage.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    //minio服务Ip地址
    private String endpoint;
    //minio服务端口
    private int port;
    //minio接入用户名
    private String accessKey;
    //minio接入密码
    private String secretKey;
    //通讯方式是否为https
    //private boolean secure;
    //存储桶名
    private String bucketName;
}
