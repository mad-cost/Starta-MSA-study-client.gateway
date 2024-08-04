package com.spring_cloud.eureka.client.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.logging.Logger;

@Component
public class CustomPreFilter implements GlobalFilter, Ordered {
  private static final Logger logger = Logger.getLogger(CustomPreFilter.class.getName());

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    logger.info("Pre Filter: Request URI: " + request.getURI());
    // 다음 필터로 전달
    return chain.filter(exchange);
  }

  @Override
  public int getOrder() {
    // 필터 순서를 가장 높은 우선 순위로 설정
    return Ordered.HIGHEST_PRECEDENCE;
  }
}
