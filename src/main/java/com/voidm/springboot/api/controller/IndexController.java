package com.voidm.springboot.api.controller;

import com.alibaba.fastjson.JSONObject;
import com.voidm.springboot.api.constant.Constants;
import com.voidm.springboot.websocket.WebSocketEventType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
@RequestMapping("/")
public class IndexController {
    @Autowired
    private SimpMessagingTemplate messageTemplate;

    @RequestMapping("interface")
    @ResponseBody
    public String getChineseHtml() {
        return new Date().toString();
    }

    @RequestMapping("{page}")
    public String restFul(@PathVariable String page) {
        return page;
    }

    @RequestMapping({"", "/", "page"})
    public String hello() {
        return "index";
    }

    //一对多推送消息
    // @Scheduled(fixedRate = 30000)
    public void sendTopicMessage() {
        long millis = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", WebSocketEventType.NOTICE);
        jsonObject.put("text","Server -> Client 一对多" + millis);
        this.messageTemplate.convertAndSend("/topic/getResponse", jsonObject);
    }

    //一对一推送消息
    // @Scheduled(fixedRate = 30000)
    public void sendQueueMessage() {
        long millis = System.currentTimeMillis();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", WebSocketEventType.NOTICE);
        jsonObject.put("text","Server -> Client 一对一" + millis);
        for (String user : Constants.users) {
            this.messageTemplate.convertAndSendToUser(user, "/queue/getResponse", jsonObject);
        }
    }
}