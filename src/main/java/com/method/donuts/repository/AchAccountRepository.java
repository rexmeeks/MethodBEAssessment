package com.method.donuts.repository;

import com.method.donuts.entities.AchAccountMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface AchAccountRepository extends CrudRepository<AchAccountMapping, String> {
    //todo implement find by 3 ids
    Collection<AchAccountMapping> findAllByDunkinId(String dunkinId);
}
