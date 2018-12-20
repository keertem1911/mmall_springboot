package com.xiyou.edu.sessionserver.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author 王传鑫
 * @Date 2018/12/19
 */
public class CookUtils {

    public static String get(HttpServletRequest request,String key){
        Map<String,Cookie> map=getCookMap(request);
        Cookie cookie=map.get(key);
        if(cookie!=null){
            return cookie.getValue();
        }else{
            return null;
        }

    }
    public static Map<String,Cookie> getCookMap(HttpServletRequest request){
        Map<String,Cookie> map= new HashMap(request.getCookies().length);
        for (Cookie cookie : request.getCookies()) {
                map.put(cookie.getName(),cookie);
        }
        return map;
    }
    public static void put(HttpServletResponse response,String key,String value,int maxAge){
        Cookie cookie=new Cookie(key,value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}
