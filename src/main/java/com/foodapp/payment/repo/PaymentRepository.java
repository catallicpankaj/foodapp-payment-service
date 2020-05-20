package com.foodapp.payment.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.payment.dto.Payment;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
	
	Payment findByOrderId(String orderId);

}
