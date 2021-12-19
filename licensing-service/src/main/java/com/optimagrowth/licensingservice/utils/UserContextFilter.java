package com.optimagrowth.licensingservice.utils;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UserContextFilter implements Filter {

  private static final Logger logger = LoggerFactory.getLogger(UserContextFilter.class);

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) servletRequest;

    UserContextHolder.getContext().setCorrelationId(request.getHeader(UserContext.CORRELATION_ID));
    UserContextHolder.getContext().setUserId(request.getHeader(UserContext.USER_ID));
    UserContextHolder.getContext().setAuthToken(request.getHeader(UserContext.AUTH_TOKEN));
    UserContextHolder.getContext()
        .setOrganizationId(request.getHeader(UserContext.ORGANIZATION_ID));

    logger.debug(
        "UserContextFilter Correlation id: {}", UserContextHolder.getContext().getCorrelationId());

    filterChain.doFilter(request, servletResponse);
  }
}
