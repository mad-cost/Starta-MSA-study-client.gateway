package com.spring_cloud.eureka.client.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Slf4j
@Component
public class LocalJwtAuthenticationFilter implements GlobalFilter {

  @Value("${service.jwt.secret-key}")
  private String secretKey;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    // signIn(로그인)페이지 접근에 대해서는 토큰을 검사를 하지 않고 넘겨준다.
    String path = exchange.getRequest().getURI().getPath();
    if (path.equals("/auth/signIn")){
      return chain.filter(exchange);
    }

    String token = extractToken(exchange);

    // 잘못된 토큰일 경우 / State: 401
    if (token == null || !validateToken(token)){
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }
    // 제대로 된 토큰일 경우 다음 필터로 이동
    return chain.filter(exchange);
  }

  private String extractToken(ServerWebExchange exchange){
    // 헤더에서 토큰을 가져오기 / Authorization: {Key:Value}형식의 Key 값
    String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
    // 토큰이 null이 아니고, "Bearer "로 시작하는지
    if (authHeader != null && authHeader.startsWith("Bearer ")){
      return authHeader.substring(7); // 순수 토큰 값
    }
    return null;
  }

  // 토큰 유효성 검사
  private boolean validateToken(String token) {
    try {
      SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
      Jws<Claims> claimsJws = Jwts.parser()
              .verifyWith(key)
              .build()
              .parseSignedClaims(token);
      // payload에 들어있는 데이터 확인
      log.info("#####payload :: " + claimsJws.getPayload().toString());

      // 추가적인 검증 로직 추가 가능(예: 토큰 만료 여부 확인 등)을 여기에 추가할 수 있습니다.
      return true;
    } catch (Exception e) { // 토큰 검증 중 예외가 발생할 경우 false 반환
      return false;
    }
  }


}







