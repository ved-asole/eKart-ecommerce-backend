package com.vedasole.ekartecommercebackend.controller;

import com.stripe.model.checkout.Session;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.service.serviceInterface.PaymentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payment")
@CrossOrigin(
        value = {
                "http://localhost:5173",
                "https://ekart.vedasole.cloud",
                "https://ekart-shopping.netlify.app",
                "https://develop--ekart-shopping.netlify.app"
        },
        allowCredentials = "true"
)
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private static final GsonJsonParser gsonJsonParser = new GsonJsonParser();
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

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
        logger.info("Webhook received with sigHeader: {}", sigHeader);

//        try {
//            // Verify the signature
//            Webhook.Signature.verifyHeader(payload, sigHeader, endpointSecret, 300L);
//            // Handle the event (omitted for brevity)
//            logger.debug("Webhook received and verified header: {}", sigHeader);
//        } catch (SignatureVerificationException e) {
//            // Invalid signature
//            logger.debug("Webhook signature verification failed: {}", sigHeader);
//        }

        Map<String, Object> eventMap = gsonJsonParser.parseMap(payload);
        Map<String, Object> payloadMap = gsonJsonParser.parseMap(payload);

        logger.info("event_id : {}", eventMap.get("id"));

        String eventType = payloadMap.get("type").toString();

        logger.info("eventType : {}", eventType);

        if (eventType.contains("checkout.session")) paymentService.handleCheckoutSessionEvents(payloadMap);
        if (eventType.contains("payment_intent")) paymentService.handlePaymentIntentEvents(payloadMap);
        else paymentService.handleStripeEvents(payloadMap, sigHeader);
    }

}