package com.apachekakfkamicroservices.ws.ProductsMicroservice.service;

import com.apachekakfkamicroservices.ws.ProductsMicroservice.rest.CreateProductRestModel;

public interface ProductService {
	String createProduct(CreateProductRestModel productRestModel) throws Exception;
}
