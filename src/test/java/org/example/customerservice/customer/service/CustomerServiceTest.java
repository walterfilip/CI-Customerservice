package org.example.customerservice.customer.service;

import org.example.customerservice.customer.model.CreateCustomerRequest;
import org.example.customerservice.customer.model.Customer;
import org.example.customerservice.customer.model.CustomerResponse;
import org.example.customerservice.customer.model.LoginRequest;
import org.example.customerservice.customer.repository.CustomerRepository;

import org.example.customerservice.utils.encoder.Encoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CreateCustomerRequest request = new CreateCustomerRequest(
            "jens",
            "jensson",
            "hej@jens.se",
            "07012345",
            "hej"
    );
    private Customer customer = new Customer(
            "jens",
            "jensson",
            "hej@jens.se",
            "07012345",
            "hej"
    );
    LoginRequest loginRequest = new LoginRequest("hej@jens.se", "hej");

    @Test
    void createCustomerShouldReturnCreatedCustomer() {
        when(customerRepository.getCustomerByEmail(request.email()))
                .thenReturn(List.of());

        Customer savedCustomer = new Customer(
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phoneNumber(),
                request.password()
                );

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        CustomerResponse result = customerService.createCustomer(request);

        assertEquals(request.firstName(),result.firstName());
        assertEquals(request.lastName(),result.lastName());
        assertEquals(request.phoneNumber(),result.phoneNumber());
        assertEquals(request.email(),result.email());

        verify(customerRepository).getCustomerByEmail("hej@jens.se");
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void createCustomerShouldThrowExceptionWhenEmailIsAlreadyUsed() {

        when(customerRepository.getCustomerByEmail(request.email()))
                .thenReturn(List.of(new Customer()));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> customerService.createCustomer(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }


    @Test
    void getCustomerByIdShouldReturnCustomer() {
       when(customerRepository.findById(1L))
               .thenReturn(Optional.of(customer));

       CustomerResponse result = customerService.getCustomerById(1L);
       assertEquals(customer.getFirstName(),result.firstName());
       assertEquals(customer.getLastName(),result.lastName());
       assertEquals(customer.getEmail(),result.email());
       assertEquals(customer.getPhoneNumber(),result.phoneNumber());
       assertNotEquals(customer.getFirstName(),result.lastName());

    }
    @Test
    void IfCustomerDoesNotExistShouldThrowException() {
      when(customerRepository.findById(1L))
              .thenReturn(Optional.empty());


      assertThrows(RuntimeException.class,
              () -> customerService.getCustomerById(1L));
    }

    @Test
    void loginCustomerShouldReturnCustomerResponse() {

        customer.setPassword(Encoder.hashPassword(loginRequest.password()));

        when(customerRepository.findByEmail(loginRequest.email()))
                .thenReturn(customer);

        CustomerResponse result = customerService.loginCustomer(loginRequest);

        assertEquals(customer.getFirstName(),result.firstName());
        assertEquals(customer.getLastName(),result.lastName());
        assertEquals(customer.getEmail(),result.email());
        assertEquals(customer.getPhoneNumber(),result.phoneNumber());

    }

    @Test
    void loginCustomerFailedShouldThrowException() {
        LoginRequest request = new LoginRequest(loginRequest.email(), "hejsan");
        customer.setPassword(Encoder.hashPassword(loginRequest.password()));
        when(customerRepository.findByEmail(loginRequest.email()))
                .thenReturn(customer);


        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> customerService.loginCustomer(request));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

}