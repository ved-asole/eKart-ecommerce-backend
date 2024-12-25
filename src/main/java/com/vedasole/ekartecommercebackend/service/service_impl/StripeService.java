package com.vedasole.ekartecommercebackend.service.service_impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.vedasole.ekartecommercebackend.entity.*;
import com.vedasole.ekartecommercebackend.repository.AddressRepo;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.OrderStatus.ORDER_CREATED;

@Service
@Transactional
@Slf4j
public class StripeService {

    @Value("${frontendDomainUrl:http://localhost:5173}")
    private String frontendDomainUrl;
    private final OrderRepo orderRepo;
    private final AddressRepo addressRepo;

    public StripeService(
            OrderRepo orderRepo,
            AddressRepo addressRepo,
            @Value("${stripeApiKey}") String stripeApiKey
    ) {
        this.orderRepo = orderRepo;
        this.addressRepo = addressRepo;
        Stripe.apiKey = stripeApiKey;
    }

    @Transactional
    public Session createCheckoutSession(
            ShoppingCart shoppingCart
    ) throws StripeException {

        List<SessionCreateParams.LineItem> lineItemList = generateLineItems(shoppingCart);

        Order savedOrder = createOrder(shoppingCart);
        String orderId = Long.toString(savedOrder.getOrderId());
        String clientReferenceNumber = "CUST" + shoppingCart.getCustomer().getCustomerId() + "_" + orderId;

        SessionCreateParams params = SessionCreateParams.builder()
                .setCustomerCreation(SessionCreateParams.CustomerCreation.IF_REQUIRED)
                .setShippingAddressCollection(SessionCreateParams.ShippingAddressCollection.builder()
                        .addAllowedCountry(SessionCreateParams.ShippingAddressCollection.AllowedCountry.IN).build())
                .setClientReferenceId(clientReferenceNumber)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(shoppingCart.getCustomer().getEmail())
                .putMetadata("order_id", orderId)
                .putMetadata("clientReferenceNumber", clientReferenceNumber)
                .putMetadata("customer_id", Long.toString(shoppingCart.getCustomer().getCustomerId()))
                .setSuccessUrl(frontendDomainUrl + "/paymentConfirmation?success=true&session_id={CHECKOUT_SESSION_ID}&order_id=" + orderId + "&client_reference_id=" + clientReferenceNumber)
                .setCancelUrl(frontendDomainUrl + "/paymentConfirmation?canceled=true&session_id={CHECKOUT_SESSION_ID}&order_id=" + orderId + "&client_reference_id=" + clientReferenceNumber)
                .addAllLineItem(lineItemList)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(clientReferenceNumber)
                .setMaxNetworkRetries(3)
                .build();

        return Session.create(params, requestOptions);
    }

    private Order createOrder(ShoppingCart shoppingCart) {

        Address dummyAddress = addressRepo.save(new Address(
                "Dummy Address Line 1",
                "Dummy Address Line 2",
                "Dummy City",
                "Dummy State",
                "Dummy Country",
                100001
        ));

        double totalAmount = shoppingCart.getTotal() - shoppingCart.getDiscount();
        log.debug("Shopping Cart Total: {}, Discount: {}, Final Order Amount: {}", shoppingCart.getTotal(), shoppingCart.getDiscount(), totalAmount);
        log.debug("Creating order for Shopping Cart: {}", shoppingCart);

        Order order = new Order(
                101L,
                shoppingCart.getCustomer(),
                new ArrayList<>(),
                dummyAddress,
                totalAmount,
                ORDER_CREATED
        );
        Order savedOrder = orderRepo.save(order);
        List<OrderItem> orderItems = shoppingCart.getShoppingCartItems().stream()
                .map(item -> new OrderItem(
                        savedOrder,
                        item.getProduct(),
                        item.getQuantity()
                )).toList();
        savedOrder.setOrderItems(new ArrayList<>(orderItems));
        orderRepo.save(savedOrder);
        log.debug("Order created with ID: {}, Total Amount: {}", savedOrder.getOrderId(), savedOrder.getTotal());
        return savedOrder;
    }

    private List<SessionCreateParams.LineItem> generateLineItems(ShoppingCart shoppingCart) {
        return shoppingCart.getShoppingCartItems().stream()
                .map(item -> {
                    if(item.getQuantity() > 0 && item.getQuantity() <= item.getProduct().getQtyInStock())
                        return SessionCreateParams.LineItem.builder()
                                .setQuantity(item.getQuantity())
                                .setPriceData(generatePriceData(item))
                                .build();
                    else return null;
                })
                .toList();
    }

    private SessionCreateParams.LineItem.PriceData generatePriceData(ShoppingCartItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("INR")
                .setUnitAmount((long) (item.getProduct().getPrice() * (1 - (item.getProduct().getDiscount() / 100))) * 100)
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(item.getProduct().getName())
//                                                                .setDescription(item.getProduct().getDesc())
                                .addImage(item.getProduct().getImage())
                                .putMetadata("productId", Long.toString(item.getProduct().getProductId()))
                                .putMetadata("SKU", item.getProduct().getSku())
                                .build()
                ).build();
    }
}