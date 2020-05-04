package com.example.customerservices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebMvcTest(CustomersController.class)
@Import({CustomersModelAssembler.class})
class CustomersControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerRepository repository;

    @BeforeEach
    void setUp() {
        when(repository.findAll()).thenReturn(List.of(new Customer(1L, "Patrik", "Ahlqvist ", 9016596, 268),
                new Customer(2L, "Oscar", "Backlund ", 5450661, 100)));



        when(repository.findById(1L)).thenReturn(Optional.of(new Customer(1L, "Patrik", "Ahlqvist ", 9016596, 268)));
        when(repository.existsById(7L)).thenReturn(false);
        when(repository.existsById(3L)).thenReturn(true);
        when(repository.save(any(Customer.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var c = (Customer) args[0];
            return new Customer(1L, c.getName(), c.getLastName(), c.getOrderNo(), c.getPrice());
        });

    }


    @Test
    void getAllReturnsListOfAllCustomers() throws Exception {
        mockMvc.perform(
                get("http://localhost:8080/api/customers").contentType("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.customerList[0]._links.self.href", is("http://localhost:8080/api/customers/1")))
                .andExpect(jsonPath("_embedded.customerList[0].name", is("Patrik")))
                .andExpect(jsonPath("_embedded.customerList[0].lastName", is("Ahlqvist ")))
                .andExpect(jsonPath("_embedded.customerList[0].orderNo", is(9016596)))
                .andExpect(jsonPath("_embedded.customerList[0].price", is(268)))
                .andExpect(jsonPath("_embedded.customerList[1]._links.self.href", is("http://localhost:8080/api/customers/2")))
                .andExpect(jsonPath("_embedded.customerList[1].name", is("Oscar")))
                .andExpect(jsonPath("_embedded.customerList[1].lastName", is("Backlund ")))
                .andExpect(jsonPath("_embedded.customerList[1].orderNo", is(5450661)))
                .andExpect(jsonPath("_embedded.customerList[1].price", is(100)));



    }

    @Test
    @DisplayName("Calls Get method with url /api/customers/1")
    void getOnePersonWithValidIdOne() throws Exception {
        mockMvc.perform(
                get("http://localhost:8080/api/customers/1").accept("application/hal+json"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("content[0].links[2].rel", is("self")))
                .andExpect(jsonPath("_links.self.href", is("http://localhost:8080/api/customers/1")));
    }




    @Test
    @DisplayName("Calls Get method with invalid id url /api/customers/7")
    void getOnePersonWithInValidIdThree() throws Exception {
        mockMvc.perform(
                get("/api/customers/7").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    @DisplayName("Delete Customer")
    void deleteGuestReturnsOk() throws Exception {
        mockMvc.perform(
                delete("http://localhost:8080/api/customers/3"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Modify only name")
    public void patchName() throws Exception {
        mockMvc.perform(patch("http://localhost:8080/api/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Patrik2\"}"))
                .andExpect(jsonPath("name", is("Patrik2")))
                .andExpect(jsonPath("lastName", is("Ahlqvist ")))
                .andExpect(jsonPath("orderNo", is (9016596)))
                .andExpect(jsonPath("price", is(268)))
                .andExpect(status().isOk());
    }

    @Test
    void addNewCustomerWithPostReturnsCreatedCustomer() throws Exception {
        mockMvc.perform(
                post("http://localhost:8080/api/customers/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"name\":\"Patrik\",\"lastName\":\"Ahlqvist \",\"orderNo\":9016596, \"price\": 268}"))
                .andExpect(status().isCreated());
    }

}