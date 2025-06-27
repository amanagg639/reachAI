package org.crm.reachai.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_QUEUE = "email-verification-queue";
    public static final String EMAIL_EXCHANGE = "email-verification-exchange";
    public static final String EMAIL_ROUTING_KEY = "email.verification";

    @Bean
    public Queue queue() {
        return new Queue(EMAIL_QUEUE);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(EMAIL_ROUTING_KEY);
    }
}

