package com.foodapp.payment.dto;

public class PaymentDetails {

	public Upi upi;
	
	public Netbanking netbanking;
	
	public CardPayment cardPayment;
	
	public String amountPaid;

	public Upi getUpi() {
		return upi;
	}

	public void setUpi(Upi upi) {
		this.upi = upi;
	}

	public Netbanking getNetbanking() {
		return netbanking;
	}

	public void setNetbanking(Netbanking netbanking) {
		this.netbanking = netbanking;
	}

	public CardPayment getCardPayment() {
		return cardPayment;
	}

	public void setCardPayment(CardPayment cardPayment) {
		this.cardPayment = cardPayment;
	}

	public String getAmountPaid() {
		return amountPaid;
	}

	public void setAmountPaid(String amountPaid) {
		this.amountPaid = amountPaid;
	}
	
	
}
