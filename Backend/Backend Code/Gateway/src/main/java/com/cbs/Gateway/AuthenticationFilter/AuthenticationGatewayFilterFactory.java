package com.cbs.Gateway.AuthenticationFilter;

import com.cbs.Gateway.Utils.ApiResponseDto;
import com.cbs.Gateway.Utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {//AbstractGatewayFilterFactory is a base class for creating custom Gateway Filters
    private static final Logger log = LoggerFactory.getLogger(AuthenticationGatewayFilterFactory.class);//using AGFF we add filters
    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtils jwtUtils;

    // ObjectMapper to  converting DTO to JSON
    private final ObjectMapper objectMapper; //This is a class from the Jackson library use to convert java object to JSON

    public AuthenticationGatewayFilterFactory() {
        super(Config.class);
        this.objectMapper = new ObjectMapper();
        // Register the JavaTimeModule to handle LocalDateTime serialization
        this.objectMapper.registerModule(new JavaTimeModule());

    }

    @Override
    public GatewayFilter apply(AuthenticationGatewayFilterFactory.Config config) { //Gateway filter is an interface
        return ((exchange, chain) -> { // exchange---->Represents the current server exchange, providing access to the HTTP request and response
            if (validator.isSecured.test(exchange.getRequest())) {
                boolean authenticated = false;
                String errorMessage = "";

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    errorMessage = "Authorization header is missing. Please provide a Bearer token.";
                } else {//checks for header form the passed token
                    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        authHeader = authHeader.substring(7);
                    }
                    try {
                        jwtUtils.validateToken(authHeader);
                        String role = jwtUtils.extractRole(authHeader); //get the role fron the token
                        String path = exchange.getRequest().getURI().getPath(); // get the URL path
                        if (path.startsWith("/api/v1/drivers") && !"Driver".equalsIgnoreCase(role)) {
                            return sendErrorResponse(exchange.getResponse(), "Access denied for non-driver roles.");
                        }
                        if (path.startsWith("/api/v1/users") && !"User".equalsIgnoreCase(role)) {
                            return sendErrorResponse(exchange.getResponse(), "Access denied for non-user roles.");
                        }
                        authenticated = true; // Token is valid
                    } catch (Exception e) {
                        System.out.println("Invalid access...!");
                        errorMessage = "Unauthorized access Session expired: Please Log in!!!" ;
                    }
                }

                if (!authenticated) {
                    return sendErrorResponse(exchange.getResponse(), errorMessage);
                }
            }
            return chain.filter(exchange);//passes the exchange to the next filter in the chain or to the target microservice.
        });
    }


    private Mono<Void> sendErrorResponse(ServerHttpResponse response, String message) {
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        ApiResponseDto<String> errorResponse = new ApiResponseDto<>(
                true,
                message,
                HttpStatus.UNAUTHORIZED.value(),
                null
        );

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBufferFactory bufferFactory = response.bufferFactory();
            DataBuffer buffer = bufferFactory.wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            System.err.println("Error writing error response: " + e.getMessage());
            return response.setComplete();
        }
    }



    public static class Config {
    }
}
