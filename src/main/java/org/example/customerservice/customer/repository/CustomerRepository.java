package org.example.customerservice.customer.repository;


import org.example.customerservice.customer.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Customer findByEmail(String email);
    List<Customer> getCustomerByEmail(String email);

}
