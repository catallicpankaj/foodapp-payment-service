package com.foodapp.payment.dto;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FundAndOrdersAudit {

	String orderId;
	String customerId;
	float totalPayment;

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public float getTotalPayment() {
		return totalPayment;
	}

	public void setTotalPayment(float totalPayment) {
		this.totalPayment = totalPayment;
	}

}
