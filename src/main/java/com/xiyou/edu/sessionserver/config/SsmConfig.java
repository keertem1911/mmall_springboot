package com.xiyou.edu.sessionserver.config;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.xiyou.edu.sessionserver.bean.CCPRestSmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Description 短信发送
 * @Author 王传鑫
 * @Date 2018/12/18
 */
@Configuration
public class SsmConfig {
    @Autowired
    private CCPRestSmsConfig ccpRestSmsConfig;
    @Bean
    public  CCPRestSmsSDK getCCPRestSmsSDK(){
        CCPRestSmsSDK restAPI = new CCPRestSmsSDK();
            //******************************注释*********************************************
        //*初始化服务器地址和端口                                                       *
        //*沙盒环境（用于应用开发调试）：restAPI.init("sandboxapp.cloopen.com", "8883");*
        //*生产环境（用户应用上线使用）：restAPI.init("app.cloopen.com", "8883");       *
        //*******************************************************************************
        restAPI.init(ccpRestSmsConfig.getServerIp(), ccpRestSmsConfig.getServerPort()+"");

        //******************************注释*********************************************
        //*初始化主帐号和主帐号令牌,对应官网开发者主账号下的ACCOUNT SID和AUTH TOKEN     *
        //*ACOUNT SID和AUTH TOKEN在登陆官网后，在“应用-管理控制台”中查看开发者主账号获取*
        //*参数顺序：第一个参数是ACOUNT SID，第二个参数是AUTH TOKEN。                   *
        //*******************************************************************************
        restAPI.setAccount(ccpRestSmsConfig.getAccountSsid(), ccpRestSmsConfig.getAccountToken());


        //******************************注释*********************************************
        //*初始化应用ID                                                                 *
        //*测试开发可使用“测试Demo”的APP ID，正式上线需要使用自己创建的应用的App ID     *
        //*应用ID的获取：登陆官网，在“应用-应用列表”，点击应用名称，看应用详情获取APP ID*
        //*******************************************************************************
        restAPI.setAppId(ccpRestSmsConfig.getAppId());
        return restAPI;
    }
}
