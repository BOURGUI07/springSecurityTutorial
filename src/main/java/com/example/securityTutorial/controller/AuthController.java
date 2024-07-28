/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTutorial.controller;

import com.example.securityTutorial.models.Customer;
import com.example.securityTutorial.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author hp
 */
@RestController
@RequestMapping("/register") // don't forget to add "/register" path into the filterChain permitAll() list
@RequiredArgsConstructor
public class AuthController {
    private final CustomerService service;
    
    @PostMapping
    public ResponseEntity<Customer> register(@RequestBody Customer customer){
        var saved = service.registerCustomer(customer);
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
