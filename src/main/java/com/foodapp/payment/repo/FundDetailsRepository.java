package com.foodapp.payment.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.payment.dto.FundDetails;

@Repository
public interface FundDetailsRepository extends MongoRepository<FundDetails, String> {
	
}
