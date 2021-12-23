package com.optimagrowth.licensingservice;

import com.netflix.discovery.converters.Auto;
import com.optimagrowth.licensingservice.config.ServiceConfig;
import com.optimagrowth.licensingservice.events.model.OrganizationChangeModel;
import com.optimagrowth.licensingservice.utils.UserContextInterceptor;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

@SpringBootApplication
@RefreshScope
@EnableDiscoveryClient
@EnableFeignClients
@EnableBinding(Sink.class)
public class LicensingServiceApplication {

  private static final Logger logger = LoggerFactory.getLogger(LicensingServiceApplication.class);

  @Autowired
  ServiceConfig serviceConfig;

  public static void main(String[] args) {
    SpringApplication.run(LicensingServiceApplication.class, args);
  }

  @StreamListener(Sink.INPUT)
  public void loggerSink(OrganizationChangeModel orgChange) {
    logger.debug("Received an {} event for organization id {}",
            orgChange.getAction(), orgChange.getOrganizationId());
  }

  /*Sets up the database connection to the Redis server*/
  @Bean
  JedisConnectionFactory jedisConnectionFactory() {
    String hostname = serviceConfig.getRedisServer();
    int port = Integer.parseInt(serviceConfig.getRedisPort());
    RedisStandaloneConfiguration redisStandaloneConfiguration =
            new RedisStandaloneConfiguration(hostname,port);
    return new JedisConnectionFactory(redisStandaloneConfiguration);
  }

  /*Creates a RedisTemplate to carry out actions for the Redis server*/
  @Bean
  public RedisTemplate<String, Object> redisTemplate() {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(jedisConnectionFactory());
    return template;
  }

  @Bean
  public LocaleResolver localeResolver() {
    SessionLocaleResolver localeResolver = new SessionLocaleResolver();
    localeResolver.setDefaultLocale(Locale.US);
    return localeResolver;
  }

  @Bean
  public ResourceBundleMessageSource messageSource() {
    ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
    messageSource.setUseCodeAsDefaultMessage(true);
    messageSource.setBasenames("messages");
    return messageSource;
  }

  @LoadBalanced
  @Bean
  public RestTemplate getRestTemplate() {
    RestTemplate restTemplate = new RestTemplate();
    List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
    if (interceptors.size() == 0) {
      restTemplate.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
    } else {
      interceptors.add(new UserContextInterceptor());
      restTemplate.setInterceptors(interceptors);
    }
    return restTemplate;
  }
}
