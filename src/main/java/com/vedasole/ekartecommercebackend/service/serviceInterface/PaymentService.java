package com.vedasole.ekartecommercebackend.service.serviceInterface;

import com.stripe.model.checkout.Session;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import java.util.Map;

public interface PaymentService {

    Session createCheckoutSession(ShoppingCartDto shoppingCartDto);

    void handleCheckoutSessionEvents(Map<String, Object> payloadMap);

    void handlePaymentIntentEvents(Map<String, Object> payloadMap);

    void handleStripeEvents(Map<String, Object> payloadMap, String sigHeader);

}