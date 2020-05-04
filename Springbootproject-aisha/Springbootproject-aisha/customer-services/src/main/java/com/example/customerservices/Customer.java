package com.example.customerservices;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class
Customer {
    @Id
    @GeneratedValue
    Long id;
    String name;
    String lastName;
    int orderNo;
    int price;


    public Customer(Long id, String name, String lastName, int orderNo, int price) {
        this.name = name;
        this.lastName = lastName;
        this.orderNo = orderNo;
        this.id = id;
        this.price = price;
    }
}

