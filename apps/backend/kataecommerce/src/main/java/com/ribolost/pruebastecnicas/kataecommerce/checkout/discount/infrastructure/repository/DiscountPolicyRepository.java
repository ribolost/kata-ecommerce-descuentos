package com.ribolost.pruebastecnicas.kataecommerce.checkout.discount.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountPolicyRepository extends MongoRepository<DiscountPolicyDocument, String> {
}
