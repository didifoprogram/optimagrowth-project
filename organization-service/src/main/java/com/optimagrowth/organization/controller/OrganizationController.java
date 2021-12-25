package com.optimagrowth.organization.controller;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/organization")
public class OrganizationController {

  @Autowired OrganizationService service;


  @GetMapping(value = "/{organizationId}")
  public ResponseEntity<Organization> getOrganization(
      @PathVariable("organizationId") String organizationId) {
    return ResponseEntity.ok(service.findById(organizationId));
  }


  @PutMapping(value = "/{organizationId}")
  public void updateOrganization(
      @PathVariable("organizationId") String id, @RequestBody Organization organization) {
    service.update(organization);
  }


  @PostMapping
  public ResponseEntity<Organization> saveOrganization(@RequestBody Organization organization) {
    return ResponseEntity.ok(service.create(organization));
  }


  @RequestMapping(value = "/{organizationId}", method = RequestMethod.DELETE)
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteOrganization(
      @PathVariable("organizationId") String organizationId,
      @RequestBody Organization organization) {
    service.delete(organization);
  }
}
