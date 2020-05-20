package com.foodapp.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.payment.dto.Payment;
import com.foodapp.payment.service.PaymentService;

@RestController
public class PaymentController {

	@Autowired
	private PaymentService paymentService;
	
	@PostMapping("/v1/payment")
	public ResponseEntity<Void> createPayment (@RequestBody Payment payment){
		paymentService.createPayment(payment);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
}
