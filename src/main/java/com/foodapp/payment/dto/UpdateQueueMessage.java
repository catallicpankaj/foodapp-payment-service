package com.foodapp.payment.dto;

public class UpdateQueueMessage {

	private String orderId;
	
	private String customerId;
	
	private float totalPaymentRequired;
	
	private String orderStatus;

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


	public float getTotalPaymentRequired() {
		return totalPaymentRequired;
	}

	public void setTotalPaymentRequired(float totalPaymentRequired) {
		this.totalPaymentRequired = totalPaymentRequired;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	

}
