package com.foodapp.payment.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.payment.dto.FundDetails;
import com.foodapp.payment.service.FundDetailsService;

@RestController
public class FundDetailsController {
	
	@Autowired
	FundDetailsService fundDetailsService;

	
	@PostMapping("/v1/funds")
	public ResponseEntity<Void> updateFunds(@RequestBody FundDetails fundDetails){
		fundDetailsService.updateFundsForCustomer(fundDetails);
		
		
		return new ResponseEntity<>(HttpStatus.CREATED);
		
	}
	
}
