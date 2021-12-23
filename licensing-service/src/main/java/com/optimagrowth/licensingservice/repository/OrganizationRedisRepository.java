package com.optimagrowth.licensingservice.repository;

import com.optimagrowth.licensingservice.model.Organization;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRedisRepository extends CrudRepository<Organization,String> {

}
