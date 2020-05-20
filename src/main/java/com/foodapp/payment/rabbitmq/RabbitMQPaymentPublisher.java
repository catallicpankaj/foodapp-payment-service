package com.foodapp.payment.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.payment.dto.OrderQueueMessage;
import com.foodapp.payment.dto.PaymentQueueMessage;
import com.foodapp.payment.dto.UpdateQueueMessage;

@Component
public class RabbitMQPaymentPublisher {

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQPaymentPublisher.class);

	@Autowired
	private AmqpTemplate rabbitTemplate;
	
	@Value("${foodapp.rabbitmq.exchange.direct}")
	private String directExchange;
	
	@Value("${foodapp.rabbitmq.routingkey}")
	private String paymentQueueRoutingKey;
	
	@Value("${foodapp.rabbitmq.create.order.routingkey}")
	private String orderQueueRoutingKey;
	
	@Value("${foodapp.rabbitmq.exchange.topic}")
	private String topicExchange;
	
	@Value("${foodapp.rabbitmq.routingkey.update}")
	private String updateQueueRoutingKey;
	
	@Autowired
	@Qualifier("jacksonObjectMapper")
	private ObjectMapper objectMapper;
	
	/**
	 * Send message on PaymentQueue for further processing.
	 * Restaurant Service listens to this queue for Processing the order.
	 * 
	 * @param message
	 */
	public void sendOnPaymentQueue(PaymentQueueMessage message) {
		rabbitTemplate.convertAndSend(this.directExchange, this.paymentQueueRoutingKey, message);
		try {
			System.out.println("Message updated to PaymentQueue" + objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			LOGGER.error("Json processing of published message on PaymentQueue failed");
		}
	}
	
	/**
	 * Send message on UpdateOrderQueue - Common queue which updates order table in OrderDatabase.
	 * @param message
	 */
	public void sendOnUpdateOrderQueue(UpdateQueueMessage message) {
		rabbitTemplate.convertAndSend(this.topicExchange, this.updateQueueRoutingKey, message);
		try {
			System.out.println("Message updated to UpdateOrderQueue" + objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			LOGGER.error("Json processing of published message on UpdateOrderQueue failed");
		}
	}
	
	/**
	 * Resubmit the message on Order Queue once fund details are added for customer. 
	 * 
	 * @param message
	 */
	public void sendOnOrderQueue(OrderQueueMessage message) {
		rabbitTemplate.convertAndSend(this.directExchange, this.orderQueueRoutingKey, message);
		try {
			System.out.println("Message updated to OrderQueue for reprocessing" + objectMapper.writeValueAsString(message));
		} catch (JsonProcessingException e) {
			LOGGER.error("Json processing of published message on UpdateOrderQueue failed");
		}
	}
	
}
