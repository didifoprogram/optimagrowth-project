package com.optimagrowth.organization.service;

import com.optimagrowth.organization.events.source.SimpleSourceBean;
import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrganizationService {

  @Autowired
  OrganizationRepository organizationRepository;

  @Autowired
  SimpleSourceBean simpleSourceBean;

  public Organization findById(String organizationId) {
    Optional<Organization> opt = organizationRepository.findById(organizationId);
    simpleSourceBean.publishOrganizationChange("GET", organizationId);
    return opt.orElse(null);
  }

  public Organization create(Organization organization) {
    organization.setId(UUID.randomUUID().toString());
    organization = organizationRepository.save(organization);

    simpleSourceBean.publishOrganizationChange("SAVE", organization.getId());

    return organization;
  }

  public void update(Organization organization){
    organizationRepository.save(organization);
    simpleSourceBean.publishOrganizationChange("UPDATE", organization.getId());
  }

  public void delete(Organization organization){
    organizationRepository.deleteById(organization.getId());
    simpleSourceBean.publishOrganizationChange("DELETE", organization.getId());

  }

}
