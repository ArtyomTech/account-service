package com.tuum.account.service.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RabbitMQConfigTest {

  @Autowired private RabbitMQConfig rabbitMQConfig;
  @Autowired private TopicExchange accountExchange;
  @Autowired private Queue accountCreatedQueue;
  @Autowired private Queue transactionCreatedQueue;
  @Autowired private Queue balanceUpdatedQueue;
  @Autowired private Queue balanceCreatedQueue;
  @Autowired private Binding accountCreatedBinding;
  @Autowired private Binding transactionCreatedBinding;
  @Autowired private Binding balanceCreatedBinding;
  @Autowired private Binding balanceUpdatedBinding;
  @Autowired private RabbitTemplate rabbitTemplate;

  @Test
  void shouldCreateAccountExchange() {
    assertThat(accountExchange).isNotNull();
    assertThat(accountExchange.getName()).isEqualTo("account.exchange");
    assertThat(accountExchange.getType()).isEqualTo("topic");
  }

  @Test
  void shouldCreateQueues() {
    assertThat(accountCreatedQueue.getName()).isEqualTo("account.created.queue");
    assertThat(accountCreatedQueue.isDurable()).isTrue();

    assertThat(transactionCreatedQueue.getName()).isEqualTo("transaction.created.queue");
    assertThat(transactionCreatedQueue.isDurable()).isTrue();

    assertThat(balanceUpdatedQueue.getName()).isEqualTo("balance.updated.queue");
    assertThat(balanceUpdatedQueue.isDurable()).isTrue();

    assertThat(balanceCreatedQueue.getName()).isEqualTo("balance.created.queue");
    assertThat(balanceCreatedQueue.isDurable()).isTrue();
  }

  @Test
  void shouldCreateBindings() {
    assertThat(accountCreatedBinding.getExchange()).isEqualTo("account.exchange");
    assertThat(accountCreatedBinding.getRoutingKey()).isEqualTo("account.created");

    assertThat(transactionCreatedBinding.getExchange()).isEqualTo("account.exchange");
    assertThat(transactionCreatedBinding.getRoutingKey()).isEqualTo("transaction.created");

    assertThat(balanceCreatedBinding.getExchange()).isEqualTo("account.exchange");
    assertThat(balanceCreatedBinding.getRoutingKey()).isEqualTo("balance.created");

    assertThat(balanceUpdatedBinding.getExchange()).isEqualTo("account.exchange");
    assertThat(balanceUpdatedBinding.getRoutingKey()).isEqualTo("balance.updated");
  }

  @Test
  void shouldConfigureRabbitTemplateWithJsonConverter() {
    assertThat(rabbitTemplate).isNotNull();
    assertThat(rabbitTemplate.getMessageConverter())
        .isInstanceOf(Jackson2JsonMessageConverter.class);
  }

  @Test
  void shouldCreateMessageConverter() {
    Jackson2JsonMessageConverter converter = rabbitMQConfig.messageConverter();
    assertThat(converter).isNotNull();
  }
}
