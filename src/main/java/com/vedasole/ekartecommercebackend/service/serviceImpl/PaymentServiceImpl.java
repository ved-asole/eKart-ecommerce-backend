package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import com.vedasole.ekartecommercebackend.service.serviceInterface.PaymentService;
import com.vedasole.ekartecommercebackend.service.serviceInterface.ShoppingCartService;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import lombok.AllArgsConstructor;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;
    private final ShoppingCartService shoppingCartService;
    private final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private final OrderRepo orderRepo;

    /**
     * This method creates a Stripe checkout session for the provided shopping cart.
     *
     * @param shoppingCartDto The shopping cart data transfer object containing the customer's ID and items.
     * @return A Stripe checkout session object representing the created session.
     * @throws APIException If an error occurs while creating the checkout session.
     */
    @Override
    public Session createCheckoutSession(ShoppingCartDto shoppingCartDto) {
        ShoppingCartDto savedShoppingCartDto;
        try {
            savedShoppingCartDto = shoppingCartService.getCart(shoppingCartDto.getCustomerId());
        } catch (ResourceNotFoundException e) {
            savedShoppingCartDto = shoppingCartService.createCartWithItems(shoppingCartDto);
        }
        ShoppingCart shoppingCart = shoppingCartService.convertToShoppingCart(savedShoppingCartDto);
        try {
            return stripeService.createCheckoutSession(shoppingCart);
        } catch (StripeException ex) {
            log.error("Error creating checkout session: {}", ex.getMessage(), ex);
            throw new APIException(ex.getMessage());
        }
    }

    /**
     * This method handles the checkout session events received from the Stripe webhook.
     *
     * @param payloadMap The payload map containing the event data.
     */
    @Override
    @Transactional
    public void handleCheckoutSessionEvents(Map<String, Object> payloadMap) {
        Map<String, Object> dataMap = (Map<String, Object>) payloadMap.get("data");
        Map<String, Object> dataObjectMap = (Map<String, Object>) dataMap.get("object");
        log.debug("dataObjectMap.client_reference_id : {}" , dataObjectMap.get("client_reference_id"));
        Map<String, Object> metadataObjectMap = (Map<String, Object>) dataObjectMap.get("metadata");
        log.info("dataObjectMap.metadata.order_id : {}", metadataObjectMap.get("order_id"));
        log.info("dataObjectMap.metadata.customer_id : {}", metadataObjectMap.get("customer_id"));
        log.info("dataObjectMap.payment_status : {}", dataObjectMap.get("payment_status"));
        log.info("dataObjectMap.httpStatus : {}", dataObjectMap.get("httpStatus"));

        if (
                dataObjectMap.get("status").equals("complete")
                && metadataObjectMap.get("order_id") != null && Long.parseLong(metadataObjectMap.get("order_id").toString()) > 0
                && metadataObjectMap.get("customer_id") != null && Long.parseLong(metadataObjectMap.get("customer_id").toString()) > 0
        ) {
            long orderId = Long.parseLong(metadataObjectMap.get("order_id").toString());
            long customerId = Long.parseLong(metadataObjectMap.get("customer_id").toString());
            orderRepo.findById(orderId)
                    .ifPresentOrElse(
                            order -> {
                                if (order.getCustomer().getCustomerId() == customerId) {
                                    order.setOrderStatus(AppConstant.OrderStatus.ORDER_PLACED);
                                    orderRepo.save(order);
                                } else {
                                    log.error("Order {} is not for customerId : {}", orderId, customerId);
                                    throw new ResourceNotFoundException("Order", "customerId", customerId);
                                }
                                order.setOrderStatus(AppConstant.OrderStatus.ORDER_PLACED);
                                orderRepo.save(order);
                            },
                            () -> {
                                log.error("Order not found with id : {}", orderId);
                                throw new ResourceNotFoundException("Order", "id", customerId);
                            }
                    );
        } else if (
                dataObjectMap.get("status").equals("expired")
                && metadataObjectMap.get("order_id") != null && Long.parseLong(metadataObjectMap.get("order_id").toString()) > 0
                && metadataObjectMap.get("customer_id") != null && Long.parseLong(metadataObjectMap.get("customer_id").toString()) > 0
        ) {
            long orderId = Long.parseLong(metadataObjectMap.get("order_id").toString());
            long customerId = Long.parseLong(metadataObjectMap.get("customer_id").toString());
            orderRepo.findById(orderId)
                    .ifPresentOrElse(
                            order -> {
                                if (order.getCustomer().getCustomerId() == customerId) {
                                    order.setOrderStatus(AppConstant.OrderStatus.ORDER_EXPIRED);
                                    orderRepo.save(order);
                                } else {
                                    log.error("Order {} does not belong to customerId : {}", orderId, customerId);
                                    throw new ResourceNotFoundException("Order", "customerId", customerId);
                                }
                            },
                            () -> {
                                log.error("Order not found with id : {}", orderId);
                                throw new ResourceNotFoundException("Order", "id", customerId);
                            }
                    );
        }
        else throw new APIException("Webhook received with invalid data");
    }

    /**
     * This method handles the payment intent events received from the Stripe webhook.
     *
     * @param payloadMap The payload map containing the event data.
     */
    @Override
    @Transactional
    public void handlePaymentIntentEvents(Map<String, Object> payloadMap) {
        String eventType = payloadMap.get("type").toString();
        Map<String, Object> dataMap = (Map<String, Object>) payloadMap.get("data");
        Map<String, Object> dataObjectMap = (Map<String, Object>) dataMap.get("object");
        log.debug("dataObjectMap.client_reference_id : {}" , dataObjectMap.get("client_reference_id"));
        Map<String, Object> metadataObjectMap = (Map<String, Object>) dataObjectMap.get("metadata");
        log.info("dataObjectMap.metadata.order_id : {}", metadataObjectMap.get("order_id"));
        log.info("dataObjectMap.metadata.customer_id : {}", metadataObjectMap.get("customer_id"));
        log.info("dataObjectMap.payment_status : {}", dataObjectMap.get("payment_status"));
        log.info("dataObjectMap.httpStatus : {}", dataObjectMap.get("httpStatus"));

        if (
                eventType.contains("payment_failed")
                        && metadataObjectMap.get("order_id") != null && Long.parseLong(metadataObjectMap.get("order_id").toString()) > 0
                        && metadataObjectMap.get("customer_id") != null && Long.parseLong(metadataObjectMap.get("customer_id").toString()) > 0
        ) {
            long orderId = Long.parseLong(metadataObjectMap.get("order_id").toString());
            long customerId = Long.parseLong(metadataObjectMap.get("customer_id").toString());
            orderRepo.findById(orderId)
                    .ifPresentOrElse(
                            order -> {
                                if (order.getCustomer().getCustomerId() == customerId) {
                                    order.setOrderStatus(AppConstant.OrderStatus.ORDER_FAILED);
                                    orderRepo.save(order);
                                } else {
                                    log.error("Order {} does not belong to customerId : {}", orderId, customerId);
                                    throw new ResourceNotFoundException("Order", "customerId", customerId);
                                }
                            },
                            () -> {
                                log.error("Order not found with id : {}", orderId);
                                throw new ResourceNotFoundException("Order", "id", customerId);
                            }
                    );
        }
        else throw new APIException("Webhook received with invalid data");
    }

    /**
     * This method handles the Stripe events received from the Stripe webhook.
     *
     * @param payloadMap The payload map containing the event data.
     * @param sigHeader The Stripe signature header.
     */
    @Override
    public void handleStripeEvents(Map<String, Object> payloadMap, String sigHeader) {
        Map<String, Object> dataMap = (Map<String, Object>) payloadMap.get("data");
        Map<String, Object> dataObjectMap = (Map<String, Object>) dataMap.get("object");

        log.info("object_id : {}", dataObjectMap.get("id"));
        JSONObject dataObject = new JSONObject(dataObjectMap);
        dataObject.forEach((k, v) -> log.info("dataObject : key : {}, value : {}", k, dataObject.get(k)));
    }
}
