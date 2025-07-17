package com.cbs.Payment;

import com.cbs.Payment.dto.PaymentRequestDto; // Ensure this import is present
import com.cbs.Payment.entity.Payment;       // Ensure this import is present
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

		@Bean
		public ModelMapper modelMapper() {
			ModelMapper modelMapper = new ModelMapper();

			// Use a stricter matching strategy to avoid unintended mappings
			modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
			modelMapper.typeMap(PaymentRequestDto.class, Payment.class).addMappings(mapper -> {
				mapper.skip(Payment::setId);
			});

			return modelMapper;
		}

}