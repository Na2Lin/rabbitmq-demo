package com.tml.demo.rabbitmqdemo;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RabbitmqDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitmqDemoApplication.class, args);
	}

  @Bean
  MessageConverter createMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }


  @Bean
  MessageRecoverer messageRecoverer(RabbitTemplate rabbitTemplate) {
    // 这里的死信交换器和路由键对应前面创建的dead_letter_exchange交换器和dead_letter_key路由键
    return new RepublishMessageRecoverer(rabbitTemplate,"dead_letter_exchange", "dead_letter_key");
  }

}
