package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.demo.dto.ProductRequest;
import com.example.demo.dto.StripeResponse;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.SessionTrackingMode;

@Service
public class StripeService
{
	@Value("${stripe.secretKey}")
	private String secretkey;
	// stripe API
	// -> productName , amount, quantity, currency
	// -> return sessionId and url
	
	public StripeResponse checkoutProducts(ProductRequest productRequest) {
		Stripe.apiKey=secretkey; // for connect stripe account
		
		
		  // Create a PaymentIntent with the order amount and currency
		// kya kitna kitne price ka kahan se kharidna chahte ho.
		 SessionCreateParams.LineItem.PriceData.ProductData productData =
                 SessionCreateParams.LineItem.PriceData.ProductData.builder()
                         .setName(productRequest.getName())
                         .build();
		 
		   // Create new line item with the above product data and associated price
		 SessionCreateParams.LineItem.PriceData priceData = 
		 SessionCreateParams.LineItem.PriceData.builder()
		 .setCurrency(productRequest.getCurrency() == null ? "USD": productRequest.getCurrency())
		 .setUnitAmount(productRequest.getAmount())
		 .setProductData(productData)
		 .build();
		 
		 SessionCreateParams.LineItem lineItem =
		 SessionCreateParams.LineItem.builder()
		  .setQuantity(productRequest.getQuantity())
		  .setPriceData(priceData)
		  .build();
		 
		 //Now we need to create a session and pass the parameter to the session
		 SessionCreateParams params = 
		 SessionCreateParams.builder()
		 .setMode(SessionCreateParams.Mode.PAYMENT)
		 .setSuccessUrl("http://localhost:8080/success")
		 .setCancelUrl("http://localhost:8080/cancel")
		 .addLineItem(lineItem)
		 .build();
		
		Session session = null;
		
		try {	
			session = Session.create(params);
				
		} 
		catch (StripeException e) {
			
			System.out.println(e);	
			
		}
		
		return StripeResponse.builder()
				.status("SUCCESS")
				.messsage("Payment Section Created")
				.sessionId(session.getId())
				.sessionUrl(session.getUrl())
				.build();
	}

}
