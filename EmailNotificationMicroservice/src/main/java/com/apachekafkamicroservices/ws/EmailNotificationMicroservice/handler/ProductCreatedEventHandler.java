package com.apachekafkamicroservices.ws.EmailNotificationMicroservice.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.apachekafkamicroservices.ws.Core.ProductCreatedEvent;
import com.apachekafkamicroservices.ws.EmailNotificationMicroservice.error.NotRetryableException;
import com.apachekafkamicroservices.ws.EmailNotificationMicroservice.error.RetryableException;

@Component
@KafkaListener(topics="product-created-events-topic")
public class ProductCreatedEventHandler {
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private RestTemplate restTemplate;
	
	public ProductCreatedEventHandler(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@KafkaHandler
	public void handle(ProductCreatedEvent productCreatedEvent)
	{
		LOGGER.info("Received a new event: " + productCreatedEvent.getTitle());
		
		String requestUrl = "http://localhost:8082/response/200";
//		String requestUrl = "http://localhost:8082/response/500";
		
		try
		{
			ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, null, String.class);
			
			if(response.getStatusCode().value()==HttpStatus.OK.value())
			{
				LOGGER.info("Received response from a remove service: " + response.getBody());
			}
		}
		catch(ResourceAccessException ex)
		{
			LOGGER.error(ex.getMessage());
			throw new RetryableException(ex);
		}
		catch(HttpServerErrorException ex)
		{
			LOGGER.error(ex.getMessage());
			throw new NotRetryableException(ex);
		}
		catch(Exception ex)
		{
			LOGGER.error(ex.getMessage());
			throw new NotRetryableException(ex);
		}
	}
}
