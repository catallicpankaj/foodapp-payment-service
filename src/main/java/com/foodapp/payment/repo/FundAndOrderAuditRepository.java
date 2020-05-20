package com.foodapp.payment.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.payment.dto.FundAndOrdersAudit;

@Repository
public interface FundAndOrderAuditRepository extends MongoRepository<FundAndOrdersAudit, String>{

}
