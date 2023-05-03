package com.method.donuts.repository;

import com.method.donuts.entities.LoanAccountMapping;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface LoanAccountRepository extends CrudRepository<LoanAccountMapping, String> {
    //todo implement find by 3 ids

    Collection<LoanAccountMapping> findAllByDunkinId(String dunkinId);
}
