package com.example.deal.repository;

import com.example.deal.model.CreditEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRepository extends CrudRepository<CreditEntity, Long> {
}
