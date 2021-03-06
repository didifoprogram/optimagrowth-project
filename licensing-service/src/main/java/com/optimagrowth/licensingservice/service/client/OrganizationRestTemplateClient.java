package com.optimagrowth.licensingservice.service.client;

import com.optimagrowth.licensingservice.model.Organization;
import com.optimagrowth.licensingservice.repository.OrganizationRedisRepository;
import com.optimagrowth.licensingservice.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    @Qualifier("keycloakRestTemplate")
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrganizationRedisRepository redisRepository;

    private Organization checkRedisCache(String organizationId) {
        try {
            return redisRepository.findById(organizationId).orElse(null);
        } catch (Exception e) {
            logger.error("Error encountered while trying to retrieve organization{} check redis cache. Exception {}",
                    organizationId, e);
            return null;
        }
    }

    private void cacheOrganizationObject(Organization organization) {
        try {
            redisRepository.save(organization);
        } catch (Exception e) {
            logger.error("Unable to cache organization {} in Redis. Exception {}", organization.getId(), e);
        }
    }

    public Organization getOrganization(String organizationId) {
        logger.debug("In Licensing Service.getOrganization: {}", UserContext.getCorrelationId());

        Organization organization = checkRedisCache(organizationId);
        if (organization != null) {
            logger.debug("I have successfully retrieved an organization {} from the redis cache: {}",
                    organizationId, organization);
            return organization;
        }
        logger.debug("Unable to locate organization from the redis cache: {}.", organizationId);

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        /* 8072 is the gateway port */
                        "http://localhost:8072/organization/v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null,
                        Organization.class,
                        organizationId);
        organization = restExchange.getBody();
        if (organization != null) {
            cacheOrganizationObject(organization);
        }
        return restExchange.getBody();
    }
}
