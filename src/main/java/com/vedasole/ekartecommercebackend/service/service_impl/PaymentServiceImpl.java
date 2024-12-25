package com.vedasole.ekartecommercebackend.service.service_impl;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.vedasole.ekartecommercebackend.entity.Address;
import com.vedasole.ekartecommercebackend.entity.ShoppingCart;
import com.vedasole.ekartecommercebackend.exception.APIException;
import com.vedasole.ekartecommercebackend.exception.ResourceNotFoundException;
import com.vedasole.ekartecommercebackend.payload.CustomerDto;
import com.vedasole.ekartecommercebackend.payload.ShoppingCartDto;
import com.vedasole.ekartecommercebackend.repository.AddressRepo;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import com.vedasole.ekartecommercebackend.service.service_interface.*;
import com.vedasole.ekartecommercebackend.utility.AppConstant;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final AddressRepo addressRepo;
    private final EmailService emailService;
    private static final String ORDER_ID_STRING = "order_id";
    private static final String CUSTOMERID_STRING = "customerId";
    private static final String CUSTOMER_ID_STRING = "customer_id";
    private static final String ORDER_STRING = "Order";
    private static final String INVALID_WEBHOOK_MSG = "Invalid webhook event received";

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
     * This method handles the Stripe webhook events.
     *
     * @param stripeObject The Stripe event object received from Stripe.
     * @throws APIException If an error occurs while handling the webhook events.
     */
    @Override
    @Transactional
    public void handleStripeEvent(StripeObject stripeObject) {
        if (stripeObject instanceof Session session) this.handleCheckoutSessionEvent(session);
        else if (stripeObject instanceof PaymentIntent paymentIntent) this.handlePaymentIntentEvent(paymentIntent);
        else {
            log.info("Stripe Event received with data: {}", stripeObject.toJson());
            throw new APIException(INVALID_WEBHOOK_MSG, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This method handles the checkout session event.
     *
     * @param session The metadata of the completed checkout session.
     * @throws APIException If an error occurs while handling the completed checkout session.
     */
    @Override
    public void handleCheckoutSessionEvent(Session session) {
        Map<String, String> metadata = session.getMetadata();
        log.debug(
                "Checkout session event received with client_reference_id[{}], order_id[{}], customer_id[{}], status[{}]",
                session.getClientReferenceId(),
                metadata.get(ORDER_ID_STRING),
                metadata.get(CUSTOMER_ID_STRING),
                session.getPaymentStatus()
        );

        if ( metadata.get(ORDER_ID_STRING) != null && Long.parseLong(metadata.get(ORDER_ID_STRING)) > 0
                && metadata.get(CUSTOMER_ID_STRING) != null && Long.parseLong(metadata.get(CUSTOMER_ID_STRING)) > 0
        ) {
            if(session.getStatus().equals("complete")) handleCompletedCheckoutSession(metadata, session.getCustomerDetails().getAddress());
            else if(session.getStatus().equals("expired")) handleExpiredCheckoutSession(metadata);
        }
        else throw new APIException(INVALID_WEBHOOK_MSG, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method handles the expired checkout session event.
     *
     * @param metadata The metadata of the expired checkout session.
     * @throws APIException If an error occurs while handling the expired checkout session.
     */
    private void handleExpiredCheckoutSession(Map<String, String> metadata) {
        long orderId = Long.parseLong(metadata.get(ORDER_ID_STRING));
        long customerId = Long.parseLong(metadata.get(CUSTOMER_ID_STRING));
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

    /**
     * This method handles the completed checkout session event.
     *
     * @param metadata The metadata of the completed checkout session.
     * @param stripeAddress The stripeAddress object received from Stripe.
     * @throws APIException If an error occurs while handling the completed checkout session.
     */
    private void handleCompletedCheckoutSession(Map<String, String> metadata, com.stripe.model.Address stripeAddress) {
        long orderId = Long.parseLong(metadata.get(ORDER_ID_STRING));
        long customerId = Long.parseLong(metadata.get(CUSTOMER_ID_STRING));
        orderRepo.findById(orderId)
                .ifPresentOrElse(
                        order -> {
                            if (order.getCustomer().getCustomerId() == customerId) {
                                // Clear the user cart
                                CustomerDto customer = customerService.getCustomerById(customerId);
                                customerService.updateCustomer(customer, customerId);

                                shoppingCartItemService.deleteAllShoppingCartItems(customer.getShoppingCart().getCartId());

                                // Format order total
                                order.setTotal(Math.round(order.getTotal()));

                                // Set stripeAddress
                                Address address = new Address(
                                        order.getAddress().getAddressId(),
                                        stripeAddress.getLine1(),
                                        stripeAddress.getLine2(),
                                        stripeAddress.getCity(),
                                        stripeAddress.getState(),
                                        stripeAddress.getCountry(),
                                        Integer.parseInt(stripeAddress.getPostalCode())
                                );
                                Address savedAddress = addressRepo.save(address);
                                order.setAddress(savedAddress);
                                order.setOrderStatus(AppConstant.OrderStatus.ORDER_PLACED);
                                order = orderRepo.save(order);

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
                        },
                        () -> {
                            log.error("Order not found in complete checkout session event with id : {}", orderId);
                            throw new ResourceNotFoundException(ORDER_STRING, "id", customerId);
                        }
                );
    }

    /**
     * This method handles the payment intent events.
     *
     * @param paymentIntent The payment intent object received from Stripe.
     * @throws APIException If an error occurs while handling the payment intent events.
     */
    @Override
    public void handlePaymentIntentEvent(PaymentIntent paymentIntent) {
        Map<String, String> metadata = paymentIntent.getMetadata();
        log.debug(
                "Payment Intent event received for order_id[{}], customer_id[{}], status[{}]",
                metadata.get(ORDER_ID_STRING),
                metadata.get(CUSTOMER_ID_STRING),
                paymentIntent.getStatus()
        );

        if (
                paymentIntent.getStatus().equals("canceled")
                        && metadata.get(ORDER_ID_STRING) != null && Long.parseLong(metadata.get(ORDER_ID_STRING)) > 0
                        && metadata.get(CUSTOMER_ID_STRING) != null && Long.parseLong(metadata.get(CUSTOMER_ID_STRING)) > 0
        ) {
            long orderId = Long.parseLong(metadata.get(ORDER_ID_STRING));
            long customerId = Long.parseLong(metadata.get(CUSTOMER_ID_STRING));
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
        else throw new APIException(INVALID_WEBHOOK_MSG, HttpStatus.BAD_REQUEST);
    }
}