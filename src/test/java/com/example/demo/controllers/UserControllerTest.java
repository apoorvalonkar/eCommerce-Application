package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepo = mock(UserRepository.class);

    private CartRepository cartRepo = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder= mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObject(userController,"userRepository",userRepo);
        TestUtils.injectObject(userController,"cartRepository",cartRepo);
        TestUtils.injectObject(userController,"bCryptPasswordEncoder",encoder);

    }

    @Test
    public void test_create_user_Happy_path() {

        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        //the above line is called stubbing, which replaces the code inside when with the value in thenReturn

        CreateUserRequest createUserRequest= new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response=userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200,response.getStatusCodeValue());

        User user = response.getBody();

        assertNotNull(user);
        assertEquals(0,user.getId());
        assertEquals("test",user.getUsername());
        assertEquals("thisIsHashed",user.getPassword());
    }

    @Test
    public void test_createUser_Invalid_password(){
        when(encoder.encode("testPassword")).thenReturn("thisIshashed");
        CreateUserRequest createUserRequest= new CreateUserRequest();
        createUserRequest.setUsername("test");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");

        final ResponseEntity<User> response= userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
    }
    @Test
    public void test_create_user_mismatch_password()
    {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest req = new CreateUserRequest();
        req.setUsername("test");
        req.setPassword("testPassword");
        req.setConfirmPassword("testpassword");

        final ResponseEntity<User> response = userController.createUser(req);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void test_verifyFindByUsername_valid()
    {
        String username = "test";

        Cart cart = new Cart();

        User user = new User();
        user.setId(0);
        user.setUsername(username);
        user.setPassword("thisIsHashed");
        user.setCart(cart);

        when(userRepo.findByUsername("test")).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        user = response.getBody();
        assertEquals("test", user.getUsername());
        assertEquals(0, user.getId());
    }

    @Test
    public void test_findByUsername_Invalid(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest= new CreateUserRequest();
        createUserRequest.setUsername("useruser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        userController.createUser(createUserRequest);
        User user= new User();
        user.setUsername(createUserRequest.getUsername());
        user.setPassword("thisIsHashed");

        doReturn(user).when(userRepo).findByUsername(createUserRequest.getUsername());
        final ResponseEntity<User> response= userController.findByUserName(user.getUsername()+"abc");

        assertEquals(404,response.getStatusCode().value());

    }

    @Test
    public void test_FindByUserId(){
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest= new CreateUserRequest();
        createUserRequest.setUsername("useruser");
        createUserRequest.setPassword("testPassword");
        createUserRequest.setConfirmPassword("testPassword");
        userController.createUser(createUserRequest);
        User user= new User();
        user.setId(5l);
        user.setUsername(createUserRequest.getUsername());
        user.setPassword("thisIsHashed");
        doReturn(Optional.of(user)).when(userRepo).findById(user.getId());
        final ResponseEntity<User> response= userController.findById(user.getId());

        assertNotNull(response);
        assertEquals(200,response.getStatusCode().value());
        User responseBody= response.getBody();
        assertEquals(5l, responseBody.getId());
    }

}
