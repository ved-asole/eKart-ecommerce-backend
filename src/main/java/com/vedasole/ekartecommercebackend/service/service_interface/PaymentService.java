package com.vedasole.ekartecommercebackend.service.service_interface;

import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;

public interface PaymentService {

    Session createCheckoutSession(ShoppingCartDto shoppingCartDto);

    void handleCheckoutSessionEvent(Session session);

    void handlePaymentIntentEvent(PaymentIntent paymentIntent);

    void handleStripeEvent(StripeObject stripeObject);

}