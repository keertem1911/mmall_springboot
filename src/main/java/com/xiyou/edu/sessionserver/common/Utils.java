package com.xiyou.edu.sessionserver.common;

import com.xiyou.edu.sessionserver.constant.SessionContant;

import java.util.Random;

/**
 * @Description
 * @Author 王传鑫
 * @Date 2018/12/18
 */
public class Utils {


    public static String randomSmsCode(){
        Random random=new Random();
        StringBuffer nums= new StringBuffer();
        for (int i = 0; i < SessionContant.SMSCODE_LENGTH; i++) {

        nums.append(""+random.nextInt(10));
        }
        return nums.toString();
    }

    public static void main(String[] args) {
        System.out.println(randomSmsCode());
    }
}
