package com.vedasole.ekartecommercebackend.service.serviceImpl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import com.vedasole.ekartecommercebackend.entity.*;
import com.vedasole.ekartecommercebackend.repository.AddressRepo;
import com.vedasole.ekartecommercebackend.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static com.vedasole.ekartecommercebackend.utility.AppConstant.OrderStatus.ORDER_CREATED;

@Service
@Transactional
public class StripeService {

    @Value("${frontendDomainUrl}")
    private String DOMAIN_URL;
    @Value("${stripeApiKey}")
    private String STRIPE_API_KEY;
    private final OrderRepo orderRepo;
    private final AddressRepo addressRepo;

    public StripeService(OrderRepo orderRepo,
                         AddressRepo addressRepo) {
        this.orderRepo = orderRepo;
        this.addressRepo = addressRepo;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = STRIPE_API_KEY;
    }

    @Transactional
    public Session createCheckoutSession(
            ShoppingCart shoppingCart
    ) throws StripeException {
        List<ShoppingCartItem> shoppingCartItemDtos = shoppingCart.getShoppingCartItems();
        List<SessionCreateParams.LineItem> listItems = shoppingCartItemDtos.stream()
                .map(item -> {
                    if(item.getQuantity() > 0 && item.getQuantity() <= item.getProduct().getQtyInStock())
                        return SessionCreateParams.LineItem.builder()
                                .setQuantity(item.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency("INR")
                                                .setUnitAmount((long) (item.getProduct().getPrice() * (1 - (item.getProduct().getDiscount() / 100)))*100)
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(item.getProduct().getName())
//                                                                .setDescription(item.getProduct().getDesc())
                                                                .addImage(item.getProduct().getImage())
                                                                .putMetadata("productId", Long.toString(item.getProduct().getProductId()))
                                                                .putMetadata("SKU", item.getProduct().getSku())
                                                                .build()
                                                ).build()
                                ).build();
                    else return null;
                }).toList();


//              TODO : Setup Address for Order from UI
//                shoppingCart.getCustomer().getAddress()
        Address address = addressRepo.save(new Address(
                5,
                "Address Line 1",
                "Address Line 2",
                "City",
                "State",
                "Country",
                444101
        ));

        Order order = new Order(
                101L,
                shoppingCart.getCustomer(),
                new ArrayList<>(),
//              TODO : Setup Address for Order from UI
//                shoppingCart.getCustomer().getAddress()
                address,
                shoppingCart.getTotal() - shoppingCart.getDiscount(),
                ORDER_CREATED
        );
        Order savedOrder = orderRepo.save(order);
        List<OrderItem> orderItems = shoppingCartItemDtos.stream()
                .map(item -> new OrderItem(
                        savedOrder,
                        item.getProduct(),
                        item.getQuantity()
                )).toList();
        savedOrder.setOrderItems(new ArrayList<>(orderItems));
        orderRepo.save(savedOrder);
        String orderId = Long.toString(savedOrder.getOrderId());
        String clientReferenceNumber = "CUST" + shoppingCart.getCustomer().getCustomerId() + "_" + orderId;
//        Coupon coupon = Coupon.create(
//                CouponCreateParams.builder()
//                        .setName("100F")
//                        .setAmountOff(100L)
//                        .setCurrency("INR")
//                        .setMaxRedemptions(1L)
//                        .build()
//        );
        SessionCreateParams params = SessionCreateParams.builder()
                .setCustomerCreation(SessionCreateParams.CustomerCreation.IF_REQUIRED)
                .setClientReferenceId(clientReferenceNumber)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCustomerEmail(shoppingCart.getCustomer().getEmail())
//                .addDiscount(
//                        SessionCreateParams.Discount.builder()
//                                .setCoupon(coupon.getId())
//                                .build()
//                )
                .putMetadata("order_id", orderId)
                .putMetadata("clientReferenceNumber", clientReferenceNumber)
                .putMetadata("customer_id", Long.toString(shoppingCart.getCustomer().getCustomerId()))
                .setSuccessUrl(DOMAIN_URL + "/paymentConfirmation?success=true&session_id={CHECKOUT_SESSION_ID}&order_id=" + orderId + "&client_reference_id=" + clientReferenceNumber)
                .setCancelUrl(DOMAIN_URL + "/paymentConfirmation?canceled=true&session_id={CHECKOUT_SESSION_ID}&order_id=" + orderId + "&client_reference_id=" + clientReferenceNumber)
                .addAllLineItem(listItems)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .build();

        RequestOptions requestOptions = RequestOptions.builder()
                .setIdempotencyKey(clientReferenceNumber)
                .setMaxNetworkRetries(3)
                .build();

        return Session.create(params, requestOptions);
    }
}
