package com.vedasole.ekartecommercebackend.controller;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.LiveStripeResponseGetter;
import com.stripe.net.Webhook;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.service.service_interface.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin(value = {
        "http://localhost:5173",
        "https://ekart.vedasole.me",
        "https://ekart-shopping.netlify.app",
        "https://develop--ekart-shopping.netlify.app"
}, allowCredentials = "true")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;
    @Value("${stripe.endpoint.secret}")
    private String endpointSecret;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<String> createCheckoutSession(
        @Valid @RequestBody ShoppingCartDto shoppingCartDto
    ) {
        Session session = paymentService.createCheckoutSession(shoppingCartDto);
        return new ResponseEntity<>(
                session.getUrl(),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/webhook/stripe")
    public void handleStripeEvents(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader
    ) {
        log.debug("Webhook received with sigHeader: {}", sigHeader);
        try {
            // Verify the signature
            Webhook.Signature.verifyHeader(payload, sigHeader, endpointSecret, 300L);
            log.debug("Webhook received and verified header: {}", sigHeader);

            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            log.info("event_id : {}, event_type: {}", event.getId(), event.getType());

            StripeObject stripeObject = StripeObject.deserializeStripeObject(
                    event.getData().getObject().toJson(),
                    event.getData().getObject().getClass(),
                    new LiveStripeResponseGetter()
            );

            paymentService.handleStripeEvent(stripeObject);
            log.info("Event processed successfully: {}", event.getId());

        } catch (SignatureVerificationException e) {
            // Invalid signature
            log.error("Event signature verification failed: sigHeader[{}]", sigHeader, e);
            throw new APIException("Invalid Event- signature verification failed", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            // Other exceptions
            log.error("Error processing event: {}", e.getMessage(), e);
            throw new APIException("Error processing event", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}