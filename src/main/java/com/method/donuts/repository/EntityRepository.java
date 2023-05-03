package com.method.donuts.repository;

import com.method.donuts.entities.EntityMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntityRepository extends CrudRepository<EntityMapping, String> {
}
