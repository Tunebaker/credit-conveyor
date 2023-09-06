package com.example.deal.repository;

import com.example.deal.model.ApplicationEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends CrudRepository<ApplicationEntity, Long> {
}
