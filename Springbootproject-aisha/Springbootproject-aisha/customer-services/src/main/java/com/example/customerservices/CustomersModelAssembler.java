package com.example.customerservices;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class CustomersModelAssembler implements RepresentationModelAssembler<Customer, EntityModel<Customer>> {



    @Override
    public EntityModel<Customer> toModel(Customer customer) {
        return new EntityModel<>(customer,
                linkTo(methodOn(CustomersController.class).one(customer.getId())).withSelfRel(),
                linkTo(methodOn(CustomersController.class).all()).withRel("customers"));
    }

    @Override
    public CollectionModel<EntityModel<Customer>> toCollectionModel(Iterable<? extends Customer> entities) {
        var collection = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(collection,
                linkTo(methodOn(CustomersController.class).all()).withSelfRel());
    }
}