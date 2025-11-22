package com.tuum.account.service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${account.exchange}")
  private String exchange;

  @Bean
  public TopicExchange accountExchange() {
    return new TopicExchange(exchange);
  }

  @Bean
  public Queue accountCreatedQueue() {
    return new Queue("account.created.queue", true);
  }

  @Bean
  public Queue transactionCreatedQueue() {
    return new Queue("transaction.created.queue", true);
  }

  @Bean
  public Queue balanceUpdatedQueue() {
    return new Queue("balance.updated.queue", true);
  }

  @Bean
  public Queue balanceCreatedQueue() {
    return new Queue("balance.created.queue", true);
  }

  @Bean
  public Binding accountCreatedBinding() {
    return BindingBuilder.bind(accountCreatedQueue()).to(accountExchange()).with("account.created");
  }

  @Bean
  public Binding transactionCreatedBinding() {
    return BindingBuilder.bind(transactionCreatedQueue())
        .to(accountExchange())
        .with("transaction.created");
  }

  @Bean
  public Binding balanceCreatedBinding() {
    return BindingBuilder.bind(balanceCreatedQueue()).to(accountExchange()).with("balance.created");
  }

  @Bean
  public Binding balanceUpdatedBinding() {
    return BindingBuilder.bind(balanceUpdatedQueue()).to(accountExchange()).with("balance.updated");
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    return new Jackson2JsonMessageConverter();
  }

  @Bean
  @SuppressWarnings("null")
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    return template;
  }
}
