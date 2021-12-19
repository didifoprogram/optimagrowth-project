package com.optimagrowth.licensingservice.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.optimagrowth.licensingservice.model.License;
import com.optimagrowth.licensingservice.service.LicenseService;
import com.optimagrowth.licensingservice.utils.UserContextHolder;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "v1/organization/{organizationId}/license")
public class LicenseController {

  private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);

  @Autowired private LicenseService licenseService;

  @GetMapping(value = "/{licenseId}")
  public ResponseEntity<License> getLicense(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {

    logger.debug(
        "LicenseServiceController Correlation id: {}",
        UserContextHolder.getContext().getCorrelationId());

    License license = licenseService.getLicense(licenseId, organizationId, "");
    license.add(
        linkTo(methodOn(LicenseController.class).getLicense(organizationId, license.getLicenseId()))
            .withSelfRel(),
        linkTo(methodOn(LicenseController.class).createLicense(organizationId, license, null))
            .withRel("createLicense"),
        linkTo(methodOn(LicenseController.class).updateLicense(organizationId, license))
            .withRel("updateLicense"),
        linkTo(
                methodOn(LicenseController.class)
                    .deleteLicense(organizationId, license.getLicenseId()))
            .withRel("deleteLicense"));

    return ResponseEntity.ok(license);
  }

  @RequestMapping(value = "/{licenseId}/{clientType}", method = RequestMethod.GET)
  public License getLicenseWithClient(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId,
      @PathVariable("clientType") String clientType) {
    return licenseService.getLicense(licenseId, organizationId, clientType);
  }

  @PutMapping
  public ResponseEntity<License> updateLicense(
      @PathVariable("organizationId") String organizationId, @RequestBody License request) {

    return ResponseEntity.ok(licenseService.updateLicense(request));
  }

  @PostMapping
  public ResponseEntity<License> createLicense(
      @PathVariable("organizationId") String organizationId,
      @RequestBody License request,
      @RequestHeader(value = "Accept-Language", required = false) Locale locale) {

    return ResponseEntity.ok(licenseService.createLicense(request));
  }

  @DeleteMapping(value = "/{licenseId}")
  public ResponseEntity<String> deleteLicense(
      @PathVariable("organizationId") String organizationId,
      @PathVariable("licenseId") String licenseId) {

    return ResponseEntity.ok(licenseService.deleteLicense(licenseId));
  }

  @GetMapping()
  public List<License> getLicenses(@PathVariable("organizationId") String organizationId)
      throws TimeoutException {

    logger.debug(
        "LicenseServiceController Correlation id: {}",
        UserContextHolder.getContext().getCorrelationId());

    return licenseService.getLicensesByOrganization(organizationId);
  }
}
