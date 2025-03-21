package com.trademaster.transactions.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.queue.transactions.news}")
    private String transactionsNewsQueue;
    @Value("${rabbitmq.queue.transactions.errors}")
    private String transactionsErrorsQueue;
    @Value("${rabbitmq.exchange.transactions}")
    private String transactionsExchange;
    @Value("${rabbitmq.routingkey.transactions.news}")
    private String transactionsNewsRoutingKey;
    @Value("${rabbitmq.routingkey.transactions.errors}")
    private String transactionsErrorsRoutingKey;

    @Bean
    public Queue transactionsNewsQueue() {
        return new Queue(transactionsNewsQueue, true, false, false, dlqArgstransactions());
    }

    @Bean
    public Queue transactionsErrorsQueue() {
        return new Queue(transactionsErrorsQueue, true, false, false);
    }

    @Bean
    public Binding transactionsNewsBinding(Queue transactionsNewsQueue, DirectExchange transactionsExchange) {
        return BindingBuilder.bind(transactionsNewsQueue).to(transactionsExchange).with(transactionsNewsRoutingKey);
    }

    @Bean
    public Binding transactionsErrorsBinding(Queue transactionsErrorsQueue, DirectExchange transactionsExchange) {
        return BindingBuilder.bind(transactionsErrorsQueue).to(transactionsExchange).with(transactionsErrorsRoutingKey);
    }

    @Bean
    public DirectExchange transactionsExchange() {
        return new DirectExchange(transactionsExchange);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(2000, 2.0, 10000)
                .build());
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    public Map<String, Object> dlqArgstransactions() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", transactionsExchange);
        args.put("x-dead-letter-routing-key", transactionsErrorsRoutingKey);  
        return args;
    }
}
