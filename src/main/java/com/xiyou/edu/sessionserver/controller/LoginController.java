package com.xiyou.edu.sessionserver.controller;

import com.cloopen.rest.sdk.CCPRestSmsSDK;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xiyou.edu.sessionserver.bean.CCPRestSmsConfig;
import com.xiyou.edu.sessionserver.bean.ResultVo;
import com.xiyou.edu.sessionserver.bean.UserInfo;
import com.xiyou.edu.sessionserver.common.CookUtils;
import com.xiyou.edu.sessionserver.constant.CookiesConstant;
import com.xiyou.edu.sessionserver.constant.RedisConstant;
import com.xiyou.edu.sessionserver.constant.SessionContant;
import com.xiyou.edu.sessionserver.common.Utils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Api("登陆相关API")
public class LoginController {
    @Autowired
    DefaultKaptcha defaultKaptcha;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    CCPRestSmsSDK ccpRestSmsSDK;
    @Autowired
    CCPRestSmsConfig ccpRestSmsConfig;
    @ApiOperation(value = "图片验证码")
    @GetMapping("/captcha")
    public void defaultKaptcha(HttpServletRequest request, HttpServletResponse response){
        byte[] byteImages=null;
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        String vaildText= defaultKaptcha.createText();
        request.getSession().setAttribute(Constants.KAPTCHA_SESSION_KEY,vaildText);

        BufferedImage imageResult = defaultKaptcha.createImage(vaildText);

        try {
            ImageIO.write(imageResult,"jpg",byteArrayOutputStream);

        } catch (IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        byteImages=byteArrayOutputStream.toByteArray();
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream out=null;
        try {
             out = response.getOutputStream();
            out.write(byteImages);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @ApiOperation(value = "密码登陆")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "name",value = "用户名/邮箱",dataType = "String"),
                    @ApiImplicitParam(name = "pwd",value = "密码",dataType = "String"),
                    @ApiImplicitParam(name = "captcha",value = "图片验证码",dataType = "String")
            }
    )
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
            @ApiResponse(code = 101,message = "验证码错误")
    })
    @PostMapping("/login_pwd")
    public ResultVo loginPwd(@RequestParam("name")String name,
                             @RequestParam("pwd")String pwd,
                             @RequestParam("captcha")String captcha,
                             HttpServletRequest request
                             ){

               String code=(String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
                log.debug("输入验证码{},正确验证码{}",captcha,code);
               if(code.trim().equals(captcha.trim())){
                        UserInfo userInfo=new UserInfo();
                        userInfo.set_id(UUID.randomUUID().toString().replaceAll("-",""));
                        userInfo.setName(name);
                        request.getSession().setAttribute(SessionContant.SESSION_USER,userInfo);
               return ResultVo.success(userInfo);
               }else{
                   return ResultVo.error(101,"验证码错误");
               }

    }

    @ApiOperation(value = "短信验证码登陆")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "phone",value = "手机号",dataType = "String"),
                    @ApiImplicitParam(name = "smsCode",value = "短信验证码",dataType = "String")
            }
    )
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
            @ApiResponse(code = 101,message = "登陆超时"),
            @ApiResponse(code = 102,message = "验证码不正确"),
            @ApiResponse(code = 103,message = "验证码已过期")
    })
    @PostMapping("/login_sms")
    public ResultVo loginSms(@RequestParam("phone")String phone,
                             @RequestParam("smsCode")String smsCode,
                             HttpServletRequest request
                             ){
               String token= CookUtils.get(request,CookiesConstant.TOKEN);
              if(token==null)
                  return ResultVo.error(101,"登陆超时");
              String value= stringRedisTemplate.opsForValue().get(String.format(RedisConstant.PREFIX,token));
              if(value!=null){
                  if(value.equals(phone+"-"+smsCode)){
                      UserInfo userInfo=new UserInfo();
                      userInfo.setPhone(phone);
                      userInfo.set_id(UUID.randomUUID().toString().replaceAll("-",""));
                      request.getSession().setAttribute(SessionContant.SESSION_USER,userInfo);
                      stringRedisTemplate.opsForValue().getOperations().delete(String.format(RedisConstant.PREFIX,token));
                      return ResultVo.success(userInfo);
                  }
                  return ResultVo.error(102,"验证码不正确");
              }
              return ResultVo.error(103,"验证码已过期");
    }

    @ApiOperation(value = "发送短信验证码")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "phone",value = "手机号",dataType = "String")
            }
    )
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功")
    })
    @GetMapping("/sms_code")
    public ResultVo sendSmsCode(@RequestParam("phone")String phone,HttpServletRequest request,HttpServletResponse response){
        String smsCode= Utils.randomSmsCode();

        HashMap<String, Object> result = ccpRestSmsSDK.sendTemplateSMS(
                phone,
                ccpRestSmsConfig.getTemplateId(),
                new String[]{smsCode, ccpRestSmsConfig.getAliveTime()});
        if("000000".equals(result.get("statusCode"))){
            //正常返回输出data包体信息（map）
            String token =UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set(String.format(RedisConstant.PREFIX,token),phone+"-"+smsCode,RedisConstant.EXPIRE, TimeUnit.SECONDS);
            CookUtils.put(response, CookiesConstant.TOKEN,token,CookiesConstant.EXPIRE);
//            LocalDateTime localDateTime=LocalDateTime.now().withNano(0);
//            localDateTime=localDateTime.plus(Long.parseLong(ccpRestSmsConfig.getAliveTime()), ChronoUnit.MINUTES);
//            request.getSession().setAttribute(phone,smsCode+"-"+localDateTime.getLong(ChronoField.SECOND_OF_DAY));

           return ResultVo.success();
        }else{
            //异常返回输出错误码和错误信息
            System.out.println("错误码=" + result.get("statusCode") +" 错误信息= "+result.get("statusMsg"));
            return ResultVo.error(Integer.parseInt(result.get("statusCode")+""),result.get("statusMsg")+"");
        }

    }

    public static void main(String[] args) {
        LocalDateTime localDateTime=LocalDateTime.now().withNano(0);
        System.out.println(localDateTime);
        localDateTime=localDateTime.plus(Long.parseLong("4"), ChronoUnit.MINUTES);
        System.out.println(localDateTime);
        System.out.println(localDateTime.getLong(ChronoField.SECOND_OF_DAY));
    }
}
