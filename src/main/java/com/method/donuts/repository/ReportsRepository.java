package com.method.donuts.repository;

import com.method.donuts.entities.Reports;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportsRepository extends CrudRepository<Reports, String> {
    public List<Reports> findAllByOrderByUploadedOnDesc();
}
