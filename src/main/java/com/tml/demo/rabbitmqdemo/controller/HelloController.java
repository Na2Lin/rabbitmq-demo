package com.tml.demo.rabbitmqdemo.controller;

import java.util.Date;

import com.tml.demo.rabbitmqdemo.entity.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "hello")
public class HelloController {
    
    private static String EXCHANGE_NAME="exchange1"; // 交换器名称
    private static String ROUTER_KEY="test2"; // 绑定路由键
    private Logger log = LoggerFactory.getLogger(HelloController.class);
    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping
    public String hello() {
        Message message = new Message("tmaolin", new Date());
        log.info("发送消息{}", message);
        rabbitTemplate.convertAndSend(EXCHANGE_NAME,ROUTER_KEY,message);
        return "Hello World!";
    }
}
