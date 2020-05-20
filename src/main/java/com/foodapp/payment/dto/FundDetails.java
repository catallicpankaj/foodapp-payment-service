package com.foodapp.payment.dto;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class FundDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3522859802380505488L;
	@Id
	String customerId;
	String orderId;
	float totalFundsInWallet;
	
	@LastModifiedDate
	Date lastUpdatedAt;

	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public float getTotalFundsInWallet() {
		return totalFundsInWallet;
	}

	public void setTotalFundsInWallet(float totalFundsInWallet) {
		this.totalFundsInWallet = totalFundsInWallet;
	}

}
