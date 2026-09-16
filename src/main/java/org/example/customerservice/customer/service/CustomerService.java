package org.example.customerservice.customer.service;

import org.example.customerservice.customer.model.CustomerResponse;
import org.example.customerservice.customer.model.*;
import org.example.customerservice.customer.repository.CustomerRepository;
import org.example.customerservice.utils.encoder.Encoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public CustomerResponse createCustomer(CreateCustomerRequest request) {

            List<Customer> list = customerRepository.getCustomerByEmail(request.email());

            //Ändringen från tomt objekt kan möjligen inte fungera ihop med mastern på hotelbooking.

            if(!list.isEmpty()){
                throw new ResponseStatusException(CONFLICT, "Epostadressen är redan registrerad");
            }

            Customer customer = new Customer();

            customer.setFirstName(request.firstName());
            customer.setLastName(request.lastName());
            customer.setEmail(request.email());
            customer.setPhoneNumber(request.phoneNumber());
            customer.setPassword(Encoder.hashPassword(request.password()));

            return toResponse(customerRepository.save(customer));

    }

    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Kunden finns inte"));

        return toResponse(customer);
    }

    public CustomerResponse loginCustomer(LoginRequest request) {
        Customer customer = customerRepository.findByEmail(request.email());

        if (customer == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Fel användarnamn eller lösenord"
            );
        }

        boolean correctPassword = Encoder.checkPassword(request.password(), customer.getPassword());

        if (!correctPassword) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Fel användarnamn eller lösenord"
            );
        }

        return toResponse(customer);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }

    public CustomerResponse updateCustomer(Long customerId, UpdateCustomerRequest request) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Kunden finns inte"));

        if (request.changePassword()) {
            if (request.currentPassword() == null
                    || request.currentPassword().isBlank()
                    || request.newPassword() == null
                    || request.newPassword().isBlank()) {

                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fyll i nuvarande och nytt lösenord");
            }

            boolean correctPassword = Encoder.checkPassword(request.currentPassword(), customer.getPassword());

            if (!correctPassword) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nuvarande lösenord är felaktigt");
            }
        }

        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhoneNumber(request.phoneNumber());

        if (request.changePassword()) {
            customer.setPassword(Encoder.hashPassword(request.newPassword()));
        }

        Customer savedCustomer = customerRepository.save(customer);

        return toResponse(savedCustomer);
    }

    public void removeUser(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new RuntimeException("Kunden finns inte"));
        customerRepository.delete(customer);
    }

}