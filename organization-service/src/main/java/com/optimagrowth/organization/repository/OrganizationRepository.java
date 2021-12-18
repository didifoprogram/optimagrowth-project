package com.optimagrowth.organization.repository;

import com.optimagrowth.organization.model.Organization;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends CrudRepository<Organization,String> {
  Optional<Organization> findById(String organizationId);

}
