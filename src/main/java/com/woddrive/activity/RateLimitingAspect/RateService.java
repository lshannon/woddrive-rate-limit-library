package com.woddrive.activity.RateLimitingAspect;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RateService {
	
	private final String rateServiceUrl;
	private final RestTemplate rateServiceClient;
	private String bannedResponse;
	
	public RateService(@Value("${rateServiceUrl}") String rateServiceUrl, RestTemplate rateServiceClient) {
		this.rateServiceUrl = rateServiceUrl;
		this.rateServiceClient = rateServiceClient;
	}

	public boolean isBanned(String ip) {
		ResponseEntity<Boolean> response = rateServiceClient.getForEntity(rateServiceUrl + "/" + ip, Boolean.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody();
		}
		log.error("Unable to call the Rate Service to check if an IP is banned. URL: {}. Error: {}", rateServiceUrl, response.getStatusCode().toString());
		return true;
	}
	
	public String getBannedResponse() {
		if (bannedResponse == null) {
			ResponseEntity<String> response = rateServiceClient.getForEntity(rateServiceUrl + "/banned/default", String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				bannedResponse =  response.getBody();
			}
			log.error("Unable to call the Rate Service to get the Banned Response. URL: {}. Error: {}", rateServiceUrl, response.getStatusCode().toString());
		}
		return bannedResponse;
	}

}
