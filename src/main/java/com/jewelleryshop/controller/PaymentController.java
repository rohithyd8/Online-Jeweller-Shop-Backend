package com.jewelleryshop.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import com.jewelleryshop.exception.OrderException;
import com.jewelleryshop.exception.UserException;
import com.jewelleryshop.modal.Order;
import com.jewelleryshop.repository.OrderRepository;
import com.jewelleryshop.response.ApiResponse;
import com.jewelleryshop.response.PaymentLinkResponse;
import com.jewelleryshop.service.OrderService;
import com.jewelleryshop.service.UserService;
import com.jewelleryshop.user.domain.OrderStatus;
import com.jewelleryshop.user.domain.PaymentStatus;

@RestController
@RequestMapping("/api")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecret;

    private OrderService orderService;
    private UserService userService;
    private OrderRepository orderRepository;
    
    public PaymentController(OrderService orderService, UserService userService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/payments/{orderId}")
    public ResponseEntity<PaymentLinkResponse> createPaymentLink(@PathVariable Long orderId,
            @RequestHeader("Authorization") String jwt) 
                    throws RazorpayException, UserException, OrderException {
        
        logger.info("Received payment link creation request for order ID: {} with JWT: {}", orderId, jwt);
        
        Order order = orderService.findOrderById(orderId);

        try {
            // Instantiate Razorpay client with your key ID and secret
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

            // Create a JSON object for the payment link request
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount", order.getTotalPrice() * 100);
            paymentLinkRequest.put("currency", "INR");

            // Customer details for payment link
            JSONObject customer = new JSONObject();
            customer.put("name", order.getUser().getFirstName() + " " + order.getUser().getLastName());
            customer.put("contact", order.getUser().getMobile());
            customer.put("email", order.getUser().getEmail());
            paymentLinkRequest.put("customer", customer);

            // Notification settings
            JSONObject notify = new JSONObject();
            notify.put("sms", true);
            notify.put("email", true);
            paymentLinkRequest.put("notify", notify);

            // Reminder settings
            paymentLinkRequest.put("reminder_enable", true);

            // Callback URL and method
            paymentLinkRequest.put("callback_url", "http://localhost:4200/payment-success?order_id=" + orderId);
            paymentLinkRequest.put("callback_method", "get");

            // Create payment link
            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);
            
            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            // Log the payment link details
            logger.info("Payment link created successfully: ID = {}, URL = {}", paymentLinkId, paymentLinkUrl);

            // Update the order with the payment link details
            PaymentLinkResponse res = new PaymentLinkResponse(paymentLinkUrl, paymentLinkId);
            PaymentLink fetchedPayment = razorpay.paymentLink.fetch(paymentLinkId);
            order.setOrderId(fetchedPayment.get("order_id"));
            orderRepository.save(order);

            logger.info("Received payment link creation request for order ID: {} with JWT: {}", orderId, jwt);
            
            return new ResponseEntity<>(res, HttpStatus.ACCEPTED);
            
        } catch (RazorpayException e) {
            logger.error("Error creating payment link for order ID: {}. Error: {}", orderId, e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }

    @GetMapping("/payments")
    public ResponseEntity<ApiResponse> redirect(@RequestParam(name = "payment_id") String paymentId,
            @RequestParam("order_id") Long orderId) throws RazorpayException, OrderException {

        logger.info("Received payment success notification with payment ID: {} and order ID: {}", paymentId, orderId);
        
        RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);
        Order order = orderService.findOrderById(orderId);
        
        try {
            // Fetch payment details
            Payment payment = razorpay.payments.fetch(paymentId);
            logger.info("Fetched payment details: {}", payment);

            if ("captured".equals(payment.get("status"))) {
                // If payment is successful, update the order status
                logger.info("Payment captured for order ID: {}", orderId);
                
                order.getPaymentDetails().setPaymentId(paymentId);
                order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
                order.setOrderStatus(OrderStatus.PLACED);
                orderRepository.save(order);
                
                logger.info("Order status updated to 'PLACED' for order ID: {}", orderId);

                ApiResponse res = new ApiResponse("Your order has been placed", true);
                return new ResponseEntity<>(res, HttpStatus.OK);
            } else {
                logger.warn("Payment not captured for payment ID: {} and order ID: {}", paymentId, orderId);
                throw new RazorpayException("Payment failed or was not captured.");
            }
        } catch (Exception e) {
            logger.error("Error processing payment for payment ID: {} and order ID: {}. Error: {}", paymentId, orderId, e.getMessage());
            new RedirectView("https://shopwithzosh.vercel.app/payment/failed");
            throw new RazorpayException(e.getMessage());
        }
    }
}
