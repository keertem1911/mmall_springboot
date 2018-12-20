package com.xiyou.edu.sessionserver.controller;

import com.xiyou.edu.sessionserver.bean.ResultVo;
import com.xiyou.edu.sessionserver.bean.UserInfo;
import com.xiyou.edu.sessionserver.constant.SessionContant;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
@Slf4j
@Api("用户操作")
public class UserController {

    @ApiOperation(value = "获取登陆用户信息")
    @ApiImplicitParams(
            {}
    )
    @ApiResponses({
            @ApiResponse(code = 0,message = "成功"),
            @ApiResponse(code = 101,message = "验证码错误")
    })
    @GetMapping("/info")
    public ResultVo getInfo(HttpServletRequest request){
        log.debug("获取用户数据");
        return ResultVo.success(request.getSession().getAttribute(SessionContant.SESSION_USER));
    }
    @GetMapping("/logout")
    public ResultVo logout(HttpServletRequest request){
        UserInfo info= (UserInfo) request.getSession().getAttribute(SessionContant.SESSION_USER);
        log.info("退出登陆{}",info);
        request.getSession().removeAttribute(SessionContant.SESSION_USER);
        return ResultVo.success();
    }
}
