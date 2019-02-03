package com.voidm.springboot.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;


@Controller
@RequestMapping("/")
public class IndexController{
    @Autowired
    private SimpMessagingTemplate messageTemplate;



    @RequestMapping("interface")
    @ResponseBody
    public String getChineseHtml() {
        return new Date().toString();
    }

    @RequestMapping("{page}")
    public String restFul(@PathVariable String page) {
        System.out.println(123);

        return page;
    }

    @RequestMapping({"","/","page"})
    public String hello() {
        return "index";
    }

    //一对多推送消息
    @Scheduled(fixedRate = 3000)
    public void sendTopicMessage() {
        long millis = System.currentTimeMillis();
        this.messageTemplate.convertAndSend("/topic/getResponse",millis);
    }

    //一对一推送消息
    @Scheduled(fixedRate = 3000)
    public void sendQueueMessage() {
        long millis = System.currentTimeMillis();
        this.messageTemplate.convertAndSendToUser("aaaaaa","/queue/getResponse",millis);
    }
}