package com.example.customerservices;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class SetUpDatabase {

    @Bean
    CommandLineRunner initDatabase(CustomerRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                //New empty database, adding some countries
                log.info("Added to database " + repository.save(new Customer(0L, "Patrik", "Ahlqvist", 9016596, 268)));
                log.info("Added to database " + repository.save(new Customer(0L, "Oscar", "Backlund", 5450661, 100)));
                log.info("Added to database " + repository.save(new Customer(0L, "Lucas", "Adamsson", 1324333, 120)));
                log.info("Added to database " + repository.save(new Customer(0L, "Isabella", "Granberg", 10235455, 150)));
                log.info("Added to database " + repository.save(new Customer(0L, "Ingrid", "Henriksson", 905949, 500)));
                log.info("Added to database " + repository.save(new Customer(0L, "Hanna", "Westergren", 11716829, 900)));
            }
        };
    }

    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }


}
