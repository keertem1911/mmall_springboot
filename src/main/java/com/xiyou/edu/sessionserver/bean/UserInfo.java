package com.xiyou.edu.sessionserver.bean;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo {
    private String _id;
    private String name;
    private String phone;
    private String pwd;
}
