package com.foodapp.payment.service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.payment.dto.Payment;
import com.foodapp.payment.dto.PaymentQueueMessage;
import com.foodapp.payment.rabbitmq.RabbitMQPaymentPublisher;
import com.foodapp.payment.repo.PaymentRepository;

@Service
public class PaymentServiceImpl implements PaymentService {

	private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);
	
	@Autowired
	PaymentRepository paymentRepository;
	
	@Autowired
	RabbitMQPaymentPublisher rabbitMQPaymentPublisher;

	@Override
	public void createPayment(Payment payment) {
		LOGGER.info("Payment in progress please wait..");

		ExecutorService paymentExecutor = Executors.newSingleThreadExecutor();
		Future<String> paymentFuture = paymentExecutor.submit(() -> {
			for (int i = 10; i > 0; i -= 3) {
				LOGGER.info("Payment in progress please wait for OrderId {}", payment.orderId);
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					LOGGER.error("Thread Interrupted");

				}
			}
			return "Success";
		});
		try {
			String paymentStatus = paymentFuture.get();
			LOGGER.info("Payment status for orderId {} is {}", payment.orderId, paymentStatus);
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error("Error Occurred while processing payment");
		}
		paymentRepository.save(payment);
		publishEventOnPaymentQueue(payment);
	}

	private void publishEventOnPaymentQueue(Payment payment) {
		PaymentQueueMessage paymentQueueMessage = new PaymentQueueMessage();
		paymentQueueMessage.setOrderId(payment.getOrderId());
		paymentQueueMessage.setCustomerId(payment.getCustomerId());
		paymentQueueMessage.setOrderStatus(payment.getOrderStatus());
		rabbitMQPaymentPublisher.sendOnPaymentQueue(paymentQueueMessage);
	}

}
