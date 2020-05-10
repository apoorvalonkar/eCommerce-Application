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
import static org.mockito.Mockito.*;

public class CartControllerTest {
    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
    }

    @Test
    public void Test_addItemToCart() {
        //Add item 1
        Item item = new Item();
        item.setId(1L);
        item.setName("MacBook Pro");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        //created a cart and add the item to cart
        Cart cart = new Cart();
        cart.setId(1L);
        cart.addItem(item);

        User user = new User();
        user.setUsername("test");
        user.setPassword("TestPassword");
        user.setCart(cart);

        doReturn(Optional.of(item)).when(itemRepository).findById(1L);
        doReturn(user).when(userRepository).findByUsername(user.getUsername());

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(item.getId());
        modifyCartRequest.setUsername(user.getUsername());
        modifyCartRequest.setQuantity(3);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertEquals(200, response.getStatusCode().value());
        //Item was successfully added to the cart for user
        assertEquals(4, response.getBody().getItems().size());

    }

    @Test
    public void Test_removeItemFromCart() {
        Item item = new Item();
        item.setId(1L);
        item.setName("MacBook Pro");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.0));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        //Add the item in the cart
        Cart cart = new Cart();
        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);
        cart.addItem(item);

        User user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("TestPassword");
        user.setCart(cart);
        when(userRepository.findByUsername("test")).thenReturn(user);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(1L);
        modifyCartRequest.setUsername("test");
        modifyCartRequest.setQuantity(1);

        //remove 1 item from cart
        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertEquals(200, response.getStatusCodeValue());
        System.out.println("The number of items in cart: " + response.getBody().getItems().size());
        assertEquals(3, response.getBody().getItems().size());
    }

    @Test
    public void Test_addItemtoCartFromInvalidUser() {
        Item item = new Item();
        item.setId(1L);
        item.setName("MacBook Pro");
        item.setDescription("13 Gray");
        item.setPrice(BigDecimal.valueOf(1200.13));

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("MacBook Air");
        item2.setDescription("13 Black");
        item2.setPrice(BigDecimal.valueOf(1110.03));

        List<Item> listOfItems = new ArrayList<>();
        listOfItems.add(item);
        listOfItems.add(item2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(listOfItems);
        cart.setTotal(BigDecimal.valueOf(11230.03));

        User user = new User();
        user.setUsername("abc");
        user.setPassword("PassWord");
        user.setCart(cart);

        doReturn(user).when(userRepository).findByUsername("xyz");
        doReturn(Optional.of(item2)).when(itemRepository).findById(2L);

        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(item2.getId());
        modifyCartRequest.setQuantity(2);
        modifyCartRequest.setUsername(user.getUsername());

        final ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCode().value());
    }

}