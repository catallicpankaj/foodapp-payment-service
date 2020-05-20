package com.foodapp.payment.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.foodapp.payment.rabbitmq.RabbitMQPaymentConsumer;

@Configuration
public class RabbitMQConfig {

	@Value("${foodapp.rabbitmq.order.queue}")
	private String orderQueueName;

	@Value("${foodapp.rabbitmq.payment.queue}")
	private String paymentQueueName;
	
	@Value("${foodapp.rabbitmq.order.queue.update}")
	private String updateOrderQueueName;
	
	@Value("${foodapp.rabbitmq.exchange.direct}")
	private String directExchange;

	@Value("${foodapp.rabbitmq.exchange.topic}")
	private String topicExchange;
	
	@Value("${foodapp.rabbitmq.routingkey}")
	private String paymentQueueRoutingKey;
	
	@Value("${foodapp.rabbitmq.routingkey.update}")
	private String updateQueueRoutingKey;
	
	@Bean
	public Queue orderQueue() {
		return new Queue(this.orderQueueName, false);
	}
	
	@Bean
	public Queue paymentQueue() {
		return new Queue(this.paymentQueueName, false);
	}
	
	@Bean
	public Queue orderUpdateQueue() {
		return new Queue(this.updateOrderQueueName, false);
	}

	@Bean
	public DirectExchange exchange() {
		return new DirectExchange(this.directExchange);
	}

	@Bean
	public TopicExchange topicExchange() {
		return new TopicExchange(this.topicExchange);
	}
	
	@Bean
	public Binding paymentQueueBinding(Queue paymentQueue, DirectExchange exchange) {
		return BindingBuilder.bind(paymentQueue).to(exchange).with(this.paymentQueueRoutingKey);
	}
	
	@Bean
	public Binding orderUpdateQueueBinding(Queue orderUpdateQueue, TopicExchange exchange) {
		return BindingBuilder.bind(orderUpdateQueue).to(exchange).with(this.updateQueueRoutingKey);
	}
	
	@Bean
	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
	
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
	
	@Bean
	MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory ) {
		SimpleMessageListenerContainer simpleMessageListenerContainer = new SimpleMessageListenerContainer();
		simpleMessageListenerContainer.setConnectionFactory(connectionFactory);
		simpleMessageListenerContainer.setQueues(this.orderQueue());
		simpleMessageListenerContainer.setMessageListener(this.rabbitMQPaymentConsumer());
		return simpleMessageListenerContainer;
	}
	
	@Bean("rabbitMQPaymentConsumer")
	public RabbitMQPaymentConsumer rabbitMQPaymentConsumer() {
		return new RabbitMQPaymentConsumer(); 
	}
	
}
