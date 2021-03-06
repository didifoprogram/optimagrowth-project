package com.optimagrowth.licensingservice.service;

import com.optimagrowth.licensingservice.config.ServiceConfig;
import com.optimagrowth.licensingservice.model.License;
import com.optimagrowth.licensingservice.model.Organization;
import com.optimagrowth.licensingservice.repository.LicenseRepository;
import com.optimagrowth.licensingservice.service.client.OrganizationDiscoveryClient;
import com.optimagrowth.licensingservice.service.client.OrganizationFeignClient;
import com.optimagrowth.licensingservice.service.client.OrganizationRestTemplateClient;
import com.optimagrowth.licensingservice.utils.UserContextHolder;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class LicenseService {

  private static final Logger logger = LoggerFactory.getLogger(LicenseService.class);

  @Autowired MessageSource messages;
  @Autowired ServiceConfig config;
  @Autowired OrganizationFeignClient organizationFeignClient;
  @Autowired OrganizationRestTemplateClient organizationRestClient;
  @Autowired OrganizationDiscoveryClient organizationDiscoveryClient;
  @Autowired private LicenseRepository licenseRepository;

  public License getLicense(String licenseId, String organizationId, String clientType) {
    License license = licenseRepository.findByOrganizationIdAndLicenseId(organizationId, licenseId);
    if (null == license) {
      throw new IllegalArgumentException(
          String.format(
              messages.getMessage("license.search.error.message", null, null),
              licenseId,
              organizationId));
    }

    Organization organization = retrieveOrganizationInfo(organizationId, clientType);
    if (null != organization) {
      license.setOrganizationName(organization.getName());
      license.setContactName(organization.getContactName());
      license.setContactEmail(organization.getContactEmail());
      license.setContactPhone(organization.getContactPhone());
    }
    return license.withComment(config.getProperty());
  }

  @CircuitBreaker(name = "organizationService")
  private Organization getOrganization(String organizationId) {
    return organizationRestClient.getOrganization(organizationId);
  }

  private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
    Organization organization = null;

    switch (clientType) {
      case "feign":
        System.out.println("I am using the feign client");
        organization = organizationFeignClient.getOrganization(organizationId);
        break;
      case "rest":
        System.out.println("I am using the rest client");
        organization = getOrganization(organizationId);
        break;
      case "discovery":
        System.out.println("I am using the discovery client");
        organization = organizationDiscoveryClient.getOrganization(organizationId);
        break;
      default:
        organization = organizationRestClient.getOrganization(organizationId);
        break;
    }

    return organization;
  }

  public License createLicense(License license) {
    license.setLicenseId(UUID.randomUUID().toString());
    licenseRepository.save(license);

    return license.withComment(config.getProperty());
  }

  public License updateLicense(License license) {
    licenseRepository.save(license);

    return license.withComment(config.getProperty());
  }

  public String deleteLicense(String licenseId) {
    String responseMessage = null;
    License license = new License();
    license.setLicenseId(licenseId);
    licenseRepository.delete(license);
    responseMessage =
        String.format(messages.getMessage("license.delete.message", null, null), licenseId);
    return responseMessage;
  }

  private void randomlyRunLong() throws TimeoutException {
    Random random = new Random();
    int randomNum = random.nextInt(3) + 1;
    if (randomNum == 3) sleep();
  }

  private void sleep() throws TimeoutException {
    try {
      Thread.sleep(5000);
      throw new java.util.concurrent.TimeoutException();
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    }
  }

  @CircuitBreaker(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
  @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
  @Bulkhead(
      name = "bulkheadLicenseService", /*type = Type.THREADPOOL,*/
      fallbackMethod = "buildFallbackLicenseList")
  @Retry(name = "retryLicenseService", fallbackMethod = "buildFallbackLicenseList")
  public List<License> getLicensesByOrganization(String organizationId) throws TimeoutException {

    randomlyRunLong(); // TODO REMOVE THIS

    logger.debug(
        "LicenseService Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

    return licenseRepository.findByOrganizationId(organizationId);
  }

  private List<License> buildFallbackLicenseList(String organizationId, Throwable t) {
    List<License> fallbackList = new ArrayList<>();
    License license = new License();
    license.setLicenseId("0000000-00-00000");
    license.setOrganizationId(organizationId);
    license.setProductName("Sorry no licensing information currently available");
    fallbackList.add(license);
    return fallbackList;
  }
}
