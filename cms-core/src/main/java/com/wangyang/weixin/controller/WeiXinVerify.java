package com.wangyang.weixin.controller;

import com.wangyang.pojo.annotation.Anonymous;
import com.wangyang.weixin.pojo.TextMessage;
import com.wangyang.weixin.util.CheckUtil;
import com.wangyang.weixin.util.MessageUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

//@RestController
////@RequestMapping("/wx")
@Slf4j
public class WeiXinVerify {
    @Autowired
    private WxMpService mpService;


    @GetMapping("/test")
    @Anonymous
    public String test() throws WxErrorException {
        // this.mpService.getWxMpConfigStorage().getAppId();
        return  this.mpService.getAccessToken();
    }

    @GetMapping
    public String verify(String signature,String timestamp,String  nonce,String echostr){
        if(CheckUtil.checkSignature(signature,timestamp,nonce)){
            log.info("微信验证成功！");
            return echostr;
        }
        return echostr;
    }

    @PostMapping
    public String receiveMessage(HttpServletRequest request){
        Map<String, String> map = MessageUtil.xmlToMap(request);
        String toUserName = map.get("ToUserName");
        String fromUserName = map.get("FromUserName");
        String createTime = map.get("CreateTime");
        String msgType = map.get("MsgType");
        String content = map.get("Content");
        String msgId = map.get("MsgId");

        String msg = null;
        if(msgType.equals("text")){
            TextMessage textMessage = new TextMessage();
            textMessage.setFromUserName(toUserName);
            textMessage.setToUserName(fromUserName);
            textMessage.setMsgType("text");
            textMessage.setCreateTime(new Date().toString());
            textMessage.setContent("send"+content);

            msg = MessageUtil.textMessageToXml(textMessage);
            System.out.println(msg);
        }
        return msg;
    }
}
