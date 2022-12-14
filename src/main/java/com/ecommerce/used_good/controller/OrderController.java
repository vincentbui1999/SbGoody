package com.ecommerce.used_good.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.used_good.bean.Offer;
import com.ecommerce.used_good.bean.Order;
import com.ecommerce.used_good.bean.Shipping;
import com.ecommerce.used_good.bean.User;
import com.ecommerce.used_good.http.Response;
import com.ecommerce.used_good.service.AuthService;
import com.ecommerce.used_good.service.MailService;
import com.ecommerce.used_good.service.OfferService;
import com.ecommerce.used_good.service.OrderService;
import com.ecommerce.used_good.util.ConstantType;

@RestController
@RequestMapping("/orders")
@PreAuthorize(ConstantType.HAS_ANY_ALL_AUTHORITY)
public class OrderController 
{
    @Autowired
    private OfferService offerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthService authService;

    @Autowired
    private MailService mailService;

    @GetMapping("/is_related/{orderID}")
    public Response isRelated(@PathVariable int orderID, Authentication authentication)
    {
        User user = authService.getCurrentLoginUser(authentication);
        return this.orderService.checkAssociateWithUser(user.getId(), orderID);
    }

    @GetMapping("/{orderID}")
    public Order getOrder(@PathVariable int orderID)
    {
        return this.orderService.getOrder(orderID);
    }

    @GetMapping("/buying")
    public List<Order> getAllBuyingOrder(Authentication authentication)
    {
        User user = authService.getCurrentLoginUser(authentication);
        return this.orderService.getAllByUserID(user.getId());
    }

    @GetMapping("/selling")
    public List<Order> getAllSellingOrder(Authentication authentication)
    {
        User user = authService.getCurrentLoginUser(authentication);

        return this.orderService.getAllSellingOrder(user.getId());
    }
    
    @PostMapping("/offer/{offerID}")
    public Response createOrder(@PathVariable int offerID, @RequestBody Shipping shipping, Authentication authentication)
    {
        User user = authService.getCurrentLoginUser(authentication);
        Offer offer = offerService.getById(offerID);

        offerService.checkOwnOffer(offer, user);

        Order order = orderService.createOrder(offer, shipping, user);

        this.mailService.sendOrderHaveBeenPlaceEmail(order.getId());

        return new Response(true, "An order have been place");
    }

    @PutMapping
    public Response modifyOrder(@RequestBody Order order)
    {
        return this.orderService.modifyOrder(order);
    }
}
