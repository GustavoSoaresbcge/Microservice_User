package com.ms.user.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import tools.jackson.databind.json.JsonMapper;


@Configuration
public class RabbitMQConfig {
	
	@Bean
    public MessageConverter messageConverter() {
		JsonMapper jsonMapper = JsonMapper.builder().build();
		return new JacksonJsonMessageConverter(jsonMapper);
    }

}
