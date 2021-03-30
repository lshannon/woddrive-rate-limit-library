package com.woddrive.activity.RateLimitingAspect;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RateServiceConfig {
	
	private String rateServiceUsername;
	private String rateServicePassword;
	
	public RateServiceConfig(@Value("${rateServiceUsername}") String rateServiceUsername,@Value("${rateServicePassword}") String rateServicePassword) {
		this.rateServiceUsername = rateServiceUsername;
		this.rateServicePassword = rateServicePassword;
	}

	public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
		return restTemplateBuilder.basicAuthentication(rateServiceUsername, rateServicePassword).build();
	}

}
