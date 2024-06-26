package com.wangyang.web.controller.api;


import com.alibaba.fastjson.JSON;
import com.wangyang.common.BaseResponse;
import com.wangyang.common.exception.ObjectException;
import com.wangyang.common.utils.CMSUtils;
import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.pojo.authorize.*;
import com.wangyang.pojo.entity.Mail;
import com.wangyang.pojo.params.EmailLoginParam;
import com.wangyang.pojo.params.WxPhoneParam;
import com.wangyang.pojo.support.Token;
import com.wangyang.service.MailService;
import com.wangyang.service.authorize.IUserService;
import com.wangyang.util.TokenProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    MailService mailService;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    IUserService userService;

    @PostMapping
    public List<BaseAuthorize> pushMail(@RequestBody  Mail mailInput){
        List<BaseAuthorize> baseAuthorizes= mailService.sendEmail(mailInput);
        return baseAuthorizes;
    }
    @GetMapping("/sendCode")
    @Anonymous
    public BaseResponse sendCode(String email,HttpServletRequest request){
        if(email==null || email.equals("")){
            throw new ObjectException("邮箱不能为空！");
        }
        LocalDateTime getExpirationTime = (LocalDateTime) request.getSession().getAttribute("captcha_expiration");
        if (getExpirationTime != null && getExpirationTime.isAfter(LocalDateTime.now())) {
            // 验证码验证通过且未过期
            throw new ObjectException("验证码已发送请稍后再试！");
        }

        LocalDateTime expirationTime = CMSUtils.getExpirationTime();
        String verificationCode = CMSUtils.generateVerificationCode();
        request.getSession().setAttribute("captcha", verificationCode);
        request.getSession().setAttribute("captcha_expiration", expirationTime);
//        sendSms(phone, verificationCode);
        mailService.sendSimpleMail(email,"验证码",verificationCode);
        return BaseResponse.ok("验证码发送成功！");
    }

    @PostMapping("/loginEmail")
    @Anonymous
    @ResponseBody
    public LoginUser loginEmail(@Valid @RequestBody EmailLoginParam emailLoginParam, HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {


        String captchaText = (String) request.getSession().getAttribute("captcha");
//        String requestURI = request.getRequestURI();
        if (captchaText != null && captchaText.equalsIgnoreCase(emailLoginParam.getCaptcha())) {
            LocalDateTime expirationTime = (LocalDateTime) request.getSession().getAttribute("captcha_expiration");

            if (expirationTime != null && expirationTime.isAfter(LocalDateTime.now())) {
                WxUser wxUser = new WxUser();
                UserDetailDTO user = userService.loginEmail(emailLoginParam.getEmail());

                LoginUser loginUser = new LoginUser();
                BeanUtils.copyProperties(user,loginUser);
                Token token = tokenProvider.generateToken(user);
                loginUser.setToken(token.getToken());
                try {
                    String encodeCookie = URLEncoder.encode(JSON.toJSON(loginUser).toString(),"utf-8");
                    Cookie userCookie = new Cookie("wxUser", encodeCookie);
                    userCookie.setMaxAge(3600); // 设置过期时间为1小时
                    userCookie.setPath("/");
                    response.addCookie(userCookie);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
//                BeanUtils.copyProperties(wxPhoneParam, wxUser);
//                LoginUser loginUser = wxUserService.login(wxUser);
                // 验证码验证通过且未过期
                Cookie cookie = new Cookie("Authorization", loginUser.getToken());
                // 设置Cookie的属性（可选）
                cookie.setMaxAge(3600); // 设置过期时间为1小时
                cookie.setPath("/");
                response.addCookie(cookie);
//                return "redirect:"+wxPhoneParam.getRedirect();
                return loginUser;
            } else {
                throw  new ObjectException("验证码已过期！");

            }
        } else {
            throw  new ObjectException("验证码不正确！");
        }

    }

}
