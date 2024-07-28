/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTutorial.service;

import com.example.securityTutorial.models.Customer;
import com.example.securityTutorial.repo.CustomerRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author hp
 */
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepo repo;
    private final PasswordEncoder encoder;
    
    @Transactional
    public Customer registerCustomer(Customer customer){
        var hashedPassword = encoder.encode(customer.getPassword());
        customer.setPassword(hashedPassword);
        return repo.save(customer);
        
    }
}
