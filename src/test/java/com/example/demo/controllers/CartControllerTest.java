package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepo=mock(UserRepository.class);
    private ItemRepository itemRepo=mock(ItemRepository.class);
    private CartRepository cartRepo=mock(CartRepository.class);

    @Before
    public void setUp() throws  Exception{
        cartController= new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepo);
        TestUtils.injectObject(cartController, "itemRepository", itemRepo);
        TestUtils.injectObject(cartController, "cartRepository", cartRepo);
    }

    @Test
    public void TestAddCart(){
        Item item=new Item();
        item.setId(1l);
        item.setName("MacBook");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        Item item2=new Item();
        item2.setId(2l);
        item2.setName("MacBook");
        item2.setDescription("13 Black");
        item2.setPrice(BigDecimal.valueOf(1110.03));

        List<Item> listOfItems= new ArrayList<>();
        listOfItems.add(item);
        listOfItems.add(item2);

        Cart cart= new Cart();
        cart.setId(1l);
        cart.setItems(listOfItems);
        cart.setTotal(BigDecimal.valueOf(11230.03));

        User user= new User();
        user.setUsername("abc");
        user.setPassword("abcdefg");
        user.setCart(cart);

        doReturn(user).when(userRepo).findByUsername(user.getUsername());
        doReturn(Optional.of(item2)).when(itemRepo).findById(2l);
        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setItemId(item2.getId());
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(user.getUsername());

        final ResponseEntity<Cart> response= cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCode().value());
        Cart responseBody= response.getBody();
        assertEquals(4, responseBody.getItems().size());
    }


    @Test
    public void TestRemoveCart(){
        Item item=new Item();
        item.setId(1l);
        item.setName("MacBook");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        Item item2=new Item();
        item2.setId(2l);
        item2.setName("MacBook");
        item2.setDescription("13 Black");
        item2.setPrice(BigDecimal.valueOf(1110.03));

        List<Item> listOfItems= new ArrayList<>();
        listOfItems.add(item);
        listOfItems.add(item2);

        Cart cart= new Cart();
        cart.setId(1l);
        cart.setItems(listOfItems);
        cart.setTotal(BigDecimal.valueOf(11230.03));

        User user= new User();
        user.setUsername("abc");
        user.setPassword("abcdefg");
        user.setCart(cart);


        doReturn(user).when(userRepo).findByUsername(user.getUsername());
        doReturn(Optional.of(item)).when(itemRepo).findById(1l);
        doReturn(Optional.of(item2)).when(itemRepo).findById(2l);

        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setItemId(item2.getId());
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setQuantity(1);

        final ResponseEntity<Cart> response= cartController.removeFromcart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCode().value());
        Cart responseBody= response.getBody();
        System.out.println(responseBody);
        assertEquals(1, responseBody.getItems().size());


    }


    @Test
    public void TestAddCartInvalidUser(){
        Item item=new Item();
        item.setId(1l);
        item.setName("MacBook");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        Item item2=new Item();
        item2.setId(2l);
        item2.setName("MacBook");
        item2.setDescription("13 Black");
        item2.setPrice(BigDecimal.valueOf(1110.03));

        List<Item> listOfItems= new ArrayList<>();
        listOfItems.add(item);
        listOfItems.add(item2);

        Cart cart= new Cart();
        cart.setId(1l);
        cart.setItems(listOfItems);
        cart.setTotal(BigDecimal.valueOf(11230.03));

        User user= new User();
        user.setUsername("abc");
        user.setPassword("abcdefg");
        user.setCart(cart);

        doReturn(user).when(userRepo).findByUsername("xyz");
        doReturn(Optional.of(item2)).when(itemRepo).findById(2l);

        ModifyCartRequest modifyCartRequest=new ModifyCartRequest();
        modifyCartRequest.setItemId(item2.getId());
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(user.getUsername());

        final ResponseEntity<Cart> response= cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404,response.getStatusCode().value());
    }

}
