package com.cbs.Ride;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient  //Apply This to Registry to Eureka Server
@EnableFeignClients    // Used to Discover the Services
public class RideApplication {

    public static void main(String[] args) {
        SpringApplication.run(RideApplication.class, args);
    }
    @Bean
    public ModelMapper createModelMapperBean() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return new ModelMapper();
    }
}