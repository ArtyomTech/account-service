package com.tuum.account.service.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisher {

  private final RabbitTemplate rabbitTemplate;

  @Value("${account.exchange}")
  private String exchange;

  public void publish(String routingKey, Object event) {
    log.info(
        "Publishing event to exchange: {}, routingKey: {}, event: {}", exchange, routingKey, event);
    rabbitTemplate.convertAndSend(exchange, routingKey, event);
  }
}
