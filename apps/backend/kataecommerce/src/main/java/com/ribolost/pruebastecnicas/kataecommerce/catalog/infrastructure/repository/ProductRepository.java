package com.ribolost.pruebastecnicas.kataecommerce.catalog.infrastructure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<ProductDocument, String> {

    List<ProductDocument> findAllByIdIn(Collection<String> ids);
}
