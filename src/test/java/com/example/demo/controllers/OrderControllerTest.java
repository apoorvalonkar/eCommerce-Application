package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;


public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository= mock(UserRepository.class);
    private OrderRepository orderRepository= mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController= new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void Test_submitOrder_successfully() {
       //Item 1
        Item item=new Item();
        item.setId(1L);
        item.setName("MacBook Pro");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));
        //item 2
        Item item2=new Item();
        item2.setId(2L);
        item2.setName("MacBook Air");
        item2.setDescription("13 Black");
        item2.setPrice(BigDecimal.valueOf(1110.03));

        //create a list for items and add the item to list
        List<Item> listOfItems= new ArrayList<>();
        listOfItems.add(item);
        listOfItems.add(item2);

        //create a cart and add all the items to cart
        Cart cart= new Cart();
        cart.setId(1L);
        cart.setItems(listOfItems);
        cart.setTotal(BigDecimal.valueOf(11230.03));

        //Created a username a password and added the the items to the user account
        User user= new User();
        user.setUsername("abc");
        user.setPassword("abcdefg");
        user.setCart(cart);

        doReturn(user).when(userRepository).findByUsername(user.getUsername());
        UserOrder order = UserOrder.createFromCart(user.getCart());
        doReturn(Optional.of(order)).when(orderRepository).save(order);

        final ResponseEntity<UserOrder> response= orderController.submit(user.getUsername());

        assertNotNull(response);
        //the 200 OK request succeeded for user abc
        assertEquals(200, response.getStatusCode().value());

        UserOrder responseBody= response.getBody();
        assertEquals(2, responseBody != null ? responseBody.getItems().size() : 0);
        assertEquals(BigDecimal.valueOf(11230.03), responseBody != null ? responseBody.getTotal() : null);
    }


    @Test
    public void Test_GetOrders_historyForUser(){
        Item item=new Item();
        item.setId(1L);
        item.setName("MacBook Pro");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        Item item2=new Item();
        item2.setId(2L);
        item2.setName("MacBook Air");
        item2.setDescription("13 Black");
        item2.setPrice(BigDecimal.valueOf(1110.03));

        Item item3=new Item();
        item3.setId(3L);
        item3.setName("Iphone 8");
        item3.setDescription("Black");
        item3.setPrice(BigDecimal.valueOf(1000.03));

        List<Item> listOfItems= new ArrayList<>();
        listOfItems.add(item);
        listOfItems.add(item2);
        listOfItems.add(item3);

        Cart cart= new Cart();
        cart.setId(1L);
        cart.setItems(listOfItems);
        cart.setTotal(BigDecimal.valueOf(11230.03));

        User user= new User();
        user.setUsername("xyz");
        user.setPassword("testPassword");
        user.setCart(cart);

        doReturn(user).when(userRepository).findByUsername(user.getUsername());
        UserOrder order = UserOrder.createFromCart(user.getCart());
        List<UserOrder> list= new ArrayList<>();
        list.add(order);
        order.setId(2L);
        list.add(order);
        order.setId(3L);
        list.add(order);
        doReturn(list).when(orderRepository).findByUser(user);

        final ResponseEntity<List<UserOrder>> response= orderController.getOrdersForUser(user.getUsername());

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        List<UserOrder> responseBody= response.getBody();
        //abc's orders history fetched successfully
        assertEquals(3, responseBody != null ? responseBody.size() : 0);

    }


    @Test
    public void Test_OrderHistory_forInvalidUser(){
        Item item=new Item();
        item.setId(1L);
        item.setName("MacBook Pro");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        Cart cart= new Cart();
        cart.addItem(item);

        User user= new User();
        user.setUsername("test");
        user.setPassword("abcdefg");
        user.setCart(cart);

        when(userRepository.findByUsername("test")).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit("test1");
        assertEquals(404,response.getStatusCodeValue());

    }

}
