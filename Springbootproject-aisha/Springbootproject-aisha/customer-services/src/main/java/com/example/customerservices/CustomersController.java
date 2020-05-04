package com.example.customerservices;


import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/customers")
@Slf4j
public class CustomersController {


    HttpClient client;

    final CustomerRepository repository;
    private final CustomersModelAssembler assembler;


    public CustomersController(CustomerRepository storage, CustomersModelAssembler customersModelAssembler) {
        this.repository = storage;
        this.assembler = customersModelAssembler;
    }


    @GetMapping
    public CollectionModel<EntityModel<Customer>> all() {
        log.debug("All customers called");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<Customer>> one(@PathVariable long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        log.info("POST create Customer " + customer);
        var c = repository.save(customer);
        log.info("Saved to repository " + c);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(CustomersController.class).slash(c.getId()).toUri());

        return new ResponseEntity<>(c, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCustomer(@PathVariable Long id) {
        if (repository.existsById(id)) {

            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    ResponseEntity<Customer> replaceCustomer(@RequestBody Customer newCustomer, @PathVariable Long id) {
        return repository.findById(id)
                .map(customer -> {
                    customer.setName(newCustomer.getName());
                    repository.save(customer);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(CustomersController.class).slash(customer.getId()).toUri());
                    return new ResponseEntity<>(customer, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    ResponseEntity<Customer> modifyCustomer(@RequestBody Customer newCustomer, @PathVariable Long id) {
        return repository.findById(id)
                .map(customer -> {
                    if (newCustomer.getName() != null)
                        customer.setName(newCustomer.getName());

                    repository.save(customer);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(CustomersController.class).slash(customer.getId()).toUri());
                    return new ResponseEntity<>(customer, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
