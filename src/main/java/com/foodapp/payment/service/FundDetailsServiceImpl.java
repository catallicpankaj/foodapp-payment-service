package com.foodapp.payment.service;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.payment.dto.FundDetails;
import com.foodapp.payment.dto.Order;
import com.foodapp.payment.dto.OrderQueueMessage;
import com.foodapp.payment.rabbitmq.RabbitMQPaymentPublisher;
import com.foodapp.payment.repo.FundDetailsRepository;

@Service
public class FundDetailsServiceImpl implements FundDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FundDetailsServiceImpl.class);

	@Autowired
	private FundDetailsRepository fundDetailsRepository;

	@Autowired
	RestTemplate restTemplate;

	@Value("${rest.orderservice.url}")
	private String restOrderServiceUrl;

	@Autowired
	RabbitMQPaymentPublisher rabbitMQPaymentPublisher;

	@Override
	public void updateFundsForCustomer(FundDetails fundDetails) {
		fundDetails.setLastUpdatedAt(new Date());
		fundDetailsRepository.save(fundDetails);
		this.processPendingTransactions(fundDetails);
	}

	private void processPendingTransactions(FundDetails fundDetails) {
		Map<String, String> orderServiceParams = new HashMap<>();
		orderServiceParams.put("customerId", fundDetails.getCustomerId());
		URI targetUri = UriComponentsBuilder.fromHttpUrl(this.restOrderServiceUrl)
				.queryParam("orderStatus", "CUSTOMER_FUND_DETAILS_NOT_EXIST").buildAndExpand(orderServiceParams).toUri();
		ResponseEntity<String> responseEntity = this.restTemplate.exchange(targetUri, HttpMethod.GET,
				this.getRequestEntity(), String.class);
		ObjectMapper mapper = new ObjectMapper();

		try {
			List<Order> orderDetails = mapper.readValue(responseEntity.getBody(), new TypeReference<List<Order>>() {
			});
			if (!orderDetails.isEmpty()) {
				orderDetails.forEach(order -> {
					publishMessageOnOrderQueue(order);
				});
			}
		} catch (JsonProcessingException e) {
			LOGGER.error("Error occured while processing responseEntity -{}", responseEntity.getBody());
		}
	}

	private void publishMessageOnOrderQueue(Order order) {
		OrderQueueMessage orderQueueMessage = new OrderQueueMessage();
		orderQueueMessage.setCustomerId(order.getCustomerId());
		orderQueueMessage.setOrderId(order.getOrderId());
		orderQueueMessage.setOrderStatus("ORDER_RESUBMITTED");
		orderQueueMessage.setTotalPaymentRequired(order.getTotalPrice());
		this.rabbitMQPaymentPublisher.sendOnOrderQueue(orderQueueMessage);
	}

	private HttpEntity<String> getRequestEntity() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		headers.set("Accept-Encoding", "gzip, deflate, br");
		headers.set("Accept-Language", "en-US,en;q=0.5");
		headers.setCacheControl("no-cache");
		headers.setConnection("keep-alive");
		headers.setPragma("no-cache");
		return new HttpEntity<>("parameters", headers);
	}

}
