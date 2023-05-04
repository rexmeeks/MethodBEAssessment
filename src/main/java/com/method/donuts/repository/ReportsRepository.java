package com.method.donuts.repository;

import com.method.donuts.entities.Reports;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportsRepository extends CrudRepository<Reports, String> {
}
