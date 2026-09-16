package org.example.customerservice.customer.controller;


import org.example.customerservice.customer.model.*;
import org.example.customerservice.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.example.customerservice.customer.model.CreateCustomerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerRestController {

    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@RequestBody @Valid CreateCustomerRequest request) {
        return customerService.createCustomer(request);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PostMapping("/login")
    public ResponseEntity<CustomerResponse> loginCustomer(@RequestBody @Valid LoginRequest request) {
        CustomerResponse customer = customerService.loginCustomer(request);

        if (customer == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(customer);
    }

    @PutMapping("/{id}")
    public CustomerResponse updateCustomer(@PathVariable Long id, @RequestBody @Valid UpdateCustomerRequest request) {
        return customerService.updateCustomer(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteCustomer(@PathVariable Long id){
        customerService.removeUser(id);
    }

}
