package com.voidm.springboot.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.voidm.springboot.api.constant.Constants;
import com.voidm.springboot.websocket.entity.WebSocketEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;


/**
 * WebSocket 调试接口
 *
 * @author voidm
 */
@Controller
@RequestMapping("/")
public class WebSocketController {
    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @RequestMapping("interface")
    @ResponseBody
    public String getChineseHtml () throws NoSuchAlgorithmException, UnsupportedEncodingException {

        String before = new Date().toString();
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.update(before.getBytes("UTF8"));
        byte s[] = m.digest();
        StringBuilder after = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            after.append(Integer.toHexString((0x000000FF & s[i]) | 0xFFFFFF00).substring(6));
        }
        System.out.println("dev2");

        return before + ":" + after;
    }

    /**
     * 跳转调试页面
     */
    @RequestMapping("webSocketDemo")
    public String restFul (HttpSession session, Model model) {
        session.setAttribute("sessionId", session.getId());
        model.addAttribute("sessionId", session.getId());
        return "webSocketDemo.html";
    }

    @RequestMapping({"", "/", "page"})
    public String hello () {
        return "index";
    }

    //一对多推送消息
    @Scheduled(fixedRate = 30000)
    public void sendTopicMessage () {
        long millis = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", WebSocketEventType.NOTICE);
        jsonObject.put("text", "Server -> Client 一对多" + millis);
        this.messageTemplate.convertAndSend("/topic/getResponse", jsonObject);
    }

    //一对一推送消息
    @Scheduled(fixedRate = 30000)
    public void sendQueueMessage () {
        long millis = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", WebSocketEventType.NOTICE);
        jsonObject.put("text", "Server -> Client 一对一" + millis);
        for (String user : Constants.users) {
            this.messageTemplate.convertAndSendToUser(user, "/queue/getResponse", jsonObject);
        }
    }
}