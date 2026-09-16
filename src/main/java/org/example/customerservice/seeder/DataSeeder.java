package org.example.customerservice.seeder;

import org.example.customerservice.customer.model.Customer;
import org.example.customerservice.customer.repository.CustomerRepository;
import org.example.customerservice.utils.encoder.Encoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final CustomerRepository customerRepository;

    public DataSeeder(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public void run(String... args) {
        if (customerRepository.count() == 0) {
            customerRepository.save(new Customer("Nils", "Modig", "nils@fakemail.se", "0767777777", Encoder.hashPassword("hej")));
            customerRepository.save(new Customer("Peter", "Peterstein", "peter@fakemail.se", "0767777776", Encoder.hashPassword("hej")));
        }
    }
}
