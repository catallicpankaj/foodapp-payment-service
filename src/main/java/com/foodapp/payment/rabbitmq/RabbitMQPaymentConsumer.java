package com.foodapp.payment.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.payment.dto.FundAndOrdersAudit;
import com.foodapp.payment.dto.FundDetails;
import com.foodapp.payment.dto.PaymentQueueMessage;
import com.foodapp.payment.dto.UpdateQueueMessage;
import com.foodapp.payment.repo.FundAndOrderAuditRepository;
import com.foodapp.payment.repo.FundDetailsRepository;

public class RabbitMQPaymentConsumer implements MessageListener {

	private static final String CUSTOMER_FUND_DETAILS_NOT_EXIST = "CUSTOMER_FUND_DETAILS_NOT_EXIST";

	private static final String INSUFFICIENT_WALLET_FUNDS = "INSUFFICIENT_WALLET_FUNDS";

	private static final String ORDER_PAID = "ORDER_PAID";

	private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQPaymentConsumer.class);

	@Autowired
	private FundDetailsRepository fundDetailsRepository;

	@Autowired
	private RabbitMQPaymentPublisher rabbitMQPaymentPublisher;

	@Autowired
	private FundAndOrderAuditRepository fundAndOrderAuditRepository;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public void onMessage(Message message) {
		String receivedMessage = new String(message.getBody());
		LOGGER.info("Message Received on ORDER_UPDATE_QUEUE - {}", receivedMessage);
		UpdateQueueMessage messageOnOrderQueue = null;
		try {
			LOGGER.info("ObjectMapper is:" + objectMapper);
			messageOnOrderQueue = this.objectMapper.readValue(receivedMessage, UpdateQueueMessage.class);

		} catch (JsonProcessingException e) {
			LOGGER.error("Exception occured while processing listened message");
			e.printStackTrace();
		}

		FundDetails fundDetails = null;
		try {
			fundDetails = fundDetailsRepository.findById(messageOnOrderQueue.getCustomerId()).orElse(fundDetails);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (fundDetails == null) {
			updateOrderFailureOnQueue(messageOnOrderQueue);
		} else if (fundDetails.getTotalFundsInWallet() < messageOnOrderQueue.getTotalPaymentRequired()) {
			messageOnOrderQueue.setOrderStatus(INSUFFICIENT_WALLET_FUNDS);
			rabbitMQPaymentPublisher.sendOnUpdateOrderQueue(messageOnOrderQueue);
		} else {
			updateOrderPaidOnQueues(messageOnOrderQueue, fundDetails);
		}
	}

	private void updateOrderFailureOnQueue(UpdateQueueMessage messageOnOrderQueue) {
		messageOnOrderQueue.setOrderStatus(CUSTOMER_FUND_DETAILS_NOT_EXIST);
		rabbitMQPaymentPublisher.sendOnUpdateOrderQueue(messageOnOrderQueue);
	}

	private void updateOrderPaidOnQueues(UpdateQueueMessage messageOnOrderQueue, FundDetails fundDetails) {
		fundDetails.setTotalFundsInWallet(
				fundDetails.getTotalFundsInWallet() - messageOnOrderQueue.getTotalPaymentRequired());
		fundDetailsRepository.save(fundDetails);
		updateAuditTableDetails(messageOnOrderQueue);
		updateStatusToOrderService(messageOnOrderQueue);
		publishToPaymentQueue(messageOnOrderQueue);
	}

	private void updateStatusToOrderService(UpdateQueueMessage messageOnOrderQueue) {
		messageOnOrderQueue.setOrderStatus(ORDER_PAID);
		rabbitMQPaymentPublisher.sendOnUpdateOrderQueue(messageOnOrderQueue);
	}

	private void updateAuditTableDetails(UpdateQueueMessage messageOnOrderQueue) {
		FundAndOrdersAudit auditDetails = new FundAndOrdersAudit();
		auditDetails.setCustomerId(messageOnOrderQueue.getCustomerId());
		auditDetails.setOrderId(messageOnOrderQueue.getOrderId());
		auditDetails.setTotalPayment(messageOnOrderQueue.getTotalPaymentRequired());
		fundAndOrderAuditRepository.save(auditDetails);
	}

	private void publishToPaymentQueue(UpdateQueueMessage messageOnOrderQueue) {
		PaymentQueueMessage paymentQueueMessage = new PaymentQueueMessage();
		paymentQueueMessage.setCustomerId(messageOnOrderQueue.getCustomerId());
		paymentQueueMessage.setOrderId(messageOnOrderQueue.getOrderId());
		paymentQueueMessage.setOrderStatus(ORDER_PAID);
		rabbitMQPaymentPublisher.sendOnPaymentQueue(paymentQueueMessage);
	}
}
