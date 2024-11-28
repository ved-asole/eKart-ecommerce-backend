package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import com.vedasole.ekartecommercebackend.service.serviceInterface.*;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final StripeService stripeService;
    private final ShoppingCartService shoppingCartService;
    private final ShoppingCartItemService shoppingCartItemService;
    private final OrderRepo orderRepo;
    private final CustomerService customerService;
    private final EmailService emailService;
    private static final String ORDER_ID_STRING = "order_id";
    private static final String CUSTOMERID_STRING = "customerId";
    private static final String CUSTOMER_ID_STRING = "customer_id";
    private static final String ORDER_STRING = "Order";

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
        Map<String, Object> dataObjectMap = getDataObjectMap(payloadMap);
        Map<String, Object> metadataObjectMap = (Map<String, Object>) dataObjectMap.get("metadata");
        log.debug(
                "Checkout session event received with client_reference_id[{}], order_id[{}], customer_id[{}], payment_status[{}]",
                dataObjectMap.get("client_reference_id"),
                metadataObjectMap.get(ORDER_ID_STRING),
                metadataObjectMap.get(CUSTOMER_ID_STRING),
                dataObjectMap.get("payment_status")
        );

        if (
                dataObjectMap.get("status").equals("complete")
                && metadataObjectMap.get(ORDER_ID_STRING) != null && Long.parseLong(metadataObjectMap.get(ORDER_ID_STRING).toString()) > 0
                && metadataObjectMap.get(CUSTOMER_ID_STRING) != null && Long.parseLong(metadataObjectMap.get(CUSTOMER_ID_STRING).toString()) > 0
        ) {
            handleCompletedCheckoutSessionEvent(metadataObjectMap);
        } else if (
                dataObjectMap.get("status").equals("expired")
                && metadataObjectMap.get(ORDER_ID_STRING) != null && Long.parseLong(metadataObjectMap.get(ORDER_ID_STRING).toString()) > 0
                && metadataObjectMap.get(CUSTOMER_ID_STRING) != null && Long.parseLong(metadataObjectMap.get(CUSTOMER_ID_STRING).toString()) > 0
        ) {
            long orderId = Long.parseLong(metadataObjectMap.get(ORDER_ID_STRING).toString());
            long customerId = Long.parseLong(metadataObjectMap.get(CUSTOMER_ID_STRING).toString());
            orderRepo.findById(orderId)
                    .ifPresentOrElse(
                            order -> {
                                if (order.getCustomer().getCustomerId() == customerId) {
                                    order.setOrderStatus(AppConstant.OrderStatus.ORDER_EXPIRED);
                                    orderRepo.save(order);
                                } else {
                                    log.error("Order {} does not belong to same customerId : {}", orderId, customerId);
                                    throw new ResourceNotFoundException(ORDER_STRING, CUSTOMERID_STRING, customerId);
                                }
                            },
                            () -> {
                                log.error("Order not found in expired checkout session event with id : {}", orderId);
                                throw new ResourceNotFoundException(ORDER_STRING, "id", customerId);
                            }
                    );
        }
        else throw new APIException("Webhook received with invalid data", HttpStatus.BAD_REQUEST);
    }

    private void handleCompletedCheckoutSessionEvent(Map<String, Object> metadataObjectMap) {
        long orderId = Long.parseLong(metadataObjectMap.get(ORDER_ID_STRING).toString());
        long customerId = Long.parseLong(metadataObjectMap.get(CUSTOMER_ID_STRING).toString());
        orderRepo.findById(orderId)
                .ifPresentOrElse(
                        order -> {
                            if (order.getCustomer().getCustomerId() == customerId) {
                                // Update Order status
                                order.setOrderStatus(AppConstant.OrderStatus.ORDER_PLACED);
                                orderRepo.save(order);
                                // Clear the user cart
                                CustomerDto customer = customerService.getCustomerById(customerId);
                                customerService.updateCustomer(customer, customerId);

                                shoppingCartItemService.deleteAllShoppingCartItems(customer.getShoppingCart().getCartId());

                                // Format order total
                                order.setTotal(Math.round(order.getTotal()));

                                Context context = new Context();
                                context.setVariable("order", order);

                                //send notification to users
                                try {
                                    log.info("Sending email for Order ID: {}, Total Amount: {}", order.getOrderId(), order.getTotal());
                                    emailService.sendMimeMessage(
                                            customer.getEmail(),
                                            "Order Confirmation",
                                            context,
                                            "orderConfirmation"
                                    );
                                } catch (MessagingException e) {
                                    log.error("Failed to send order confirmation email to {}", customer.getEmail(), e);
                                    throw new APIException("Failed to send order confirmation email", e);
                                }

                            } else {
                                log.error("Order {} is not for customerId : {}", orderId, customerId);
                                throw new ResourceNotFoundException(ORDER_STRING, CUSTOMERID_STRING, customerId);
                            }
                            order.setOrderStatus(AppConstant.OrderStatus.ORDER_PLACED);
                            orderRepo.save(order);
                        },
                        () -> {
                            log.error("Order not found in complete checkout session event with id : {}", orderId);
                            throw new ResourceNotFoundException(ORDER_STRING, "id", customerId);
                        }
                );
    }

    private static Map<String, Object> getDataObjectMap(Map<String, Object> payloadMap) {
        Map<String, Object> dataMap = (Map<String, Object>) payloadMap.get("data");
        return (Map<String, Object>) dataMap.get("object");
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
        Map<String, Object> dataObjectMap = getDataObjectMap(payloadMap);
        Map<String, Object> metadataObjectMap = (Map<String, Object>) dataObjectMap.get("metadata");
        log.debug(
                "Payment Intent event received with client_reference_id[{}], order_id[{}], customer_id[{}], payment_status[{}]",
                dataObjectMap.get("client_reference_id"),
                metadataObjectMap.get(ORDER_ID_STRING),
                metadataObjectMap.get(CUSTOMER_ID_STRING),
                dataObjectMap.get("payment_status")
        );

        if (
                eventType.contains("payment_failed")
                        && metadataObjectMap.get(ORDER_ID_STRING) != null && Long.parseLong(metadataObjectMap.get(ORDER_ID_STRING).toString()) > 0
                        && metadataObjectMap.get(CUSTOMER_ID_STRING) != null && Long.parseLong(metadataObjectMap.get(CUSTOMER_ID_STRING).toString()) > 0
        ) {
            long orderId = Long.parseLong(metadataObjectMap.get(ORDER_ID_STRING).toString());
            long customerId = Long.parseLong(metadataObjectMap.get(CUSTOMER_ID_STRING).toString());
            orderRepo.findById(orderId)
                    .ifPresentOrElse(
                            order -> {
                                if (order.getCustomer().getCustomerId() == customerId) {
                                    order.setOrderStatus(AppConstant.OrderStatus.ORDER_FAILED);
                                    orderRepo.save(order);
                                } else {
                                    log.error("Order {} does not belong to customerId : {}", orderId, customerId);
                                    throw new ResourceNotFoundException(ORDER_STRING, CUSTOMERID_STRING, customerId);
                                }
                            },
                            () -> {
                                log.error("Order not found with id : {}", orderId);
                                throw new ResourceNotFoundException(ORDER_STRING, "id", customerId);
                            }
                    );
        }
        else throw new APIException("Webhook received with invalid data", HttpStatus.BAD_REQUEST);
    }

    /**
     * This method handles the Stripe events received from the Stripe webhook.
     *
     * @param payloadMap The payload map containing the event data.
     */
    @Override
    public void handleStripeEvents(Map<String, Object> payloadMap) {
        Map<String, Object> dataObjectMap = getDataObjectMap(payloadMap);

        log.info("object_id : {}", dataObjectMap.get("id"));
        JSONObject dataObject = new JSONObject(dataObjectMap);
        dataObject.forEach((k, v) -> log.info("dataObject : key : {}, value : {}", k, dataObject.get(k)));
    }
}