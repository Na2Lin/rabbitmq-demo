package com.tml.demo.rabbitmqdemo.listener;

import com.tml.demo.rabbitmqdemo.entity.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class MessageListener {

    Logger log = LoggerFactory.getLogger(MessageListener.class);

    // queues 指定监听的queue名称， concurrency指定开启多少个线程接收消息
    @RabbitListener(queues = "queue2",concurrency = "1")
    public void helloMessage(Message message) {
        log.info("收到消息{}", message);
    }
}
