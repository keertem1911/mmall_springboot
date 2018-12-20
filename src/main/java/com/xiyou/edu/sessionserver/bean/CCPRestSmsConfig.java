package com.xiyou.edu.sessionserver.bean;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @Description
 * @Author 王传鑫
 * @Date 2018/12/18
 */
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "ccprestsms")
@Data
@Component
@ToString
public class CCPRestSmsConfig {
    private String serverIp;
    private Integer serverPort;
    private String accountSsid;
    private String accountToken;
    private String appId;
    private String  templateId;
    private String aliveTime;
}
