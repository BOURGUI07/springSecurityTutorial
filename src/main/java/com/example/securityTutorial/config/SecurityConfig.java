/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.securityTutorial.config;

import com.example.securityTutorial.handlingexceptions.CustomAccessDeniedHandler;
import com.example.securityTutorial.handlingexceptions.CustomBasicAuthEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import static org.springframework.security.config.Customizer.withDefaults;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

/**
 *
 * @author hp
 */
@EnableWebSecurity
@Configuration
@Profile("!prod")
public class SecurityConfig {
    
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // handle Invalid Sessions
        // Redirect User to another page ("/invalidSession)
        // Restrict User to Have ONLY ONE Session!
        
        /*
            s.maximumSessions(1):

                This method is used to limit the maximum number of sessions a single user can have concurrently. When you set maximumSessions(1), it means that a user can only have one active session at a time. If the user tries to log in from another device or browser while already logged in elsewhere, the previous session is invalidated automatically.

                Example: If a user logs in from their desktop and then tries to log in from their mobile device, the session on the desktop will be invalidated because the new login attempt exceeds the maximum allowed sessions (which is set to 1 in this case).

            s.maxSessionsPreventsLogin(true):

                This method determines what happens when a user exceeds the maximum number of allowed sessions (as defined by maximumSessions()). When you set maxSessionsPreventsLogin(true), it means that if a user tries to log in and they already have the maximum number of sessions open (as defined by maximumSessions()), the login attempt will be denied.

                Example: If a user is allowed only one session (maximumSessions(1)), and they try to log in from a different device or browser while already logged in, the second login attempt will fail. The user will not be able to have multiple sessions concurrently because maxSessionsPreventsLogin(true) prevents them from logging in again while their previous session is active.
        */
        http.sessionManagement(s->s
                .invalidSessionUrl("/invalidSession")
                .maximumSessions(3)
                .maxSessionsPreventsLogin(true)
                .expiredUrl("/expiredSession"));
        
       
        
        // Accepting Only HTTP protocol requests
        http.requiresChannel(x -> x.anyRequest().requiresInsecure());
        
        
        // http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        // http.authorizeHttpRequests(requests -> requests.anyRequest().denyAll());
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/myAccount", "/myBalance","/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact","/error", "/register","/invalidSession","/expiredSession").permitAll()
        );
        
        
        http.formLogin(withDefaults());
        
        
        // if we don't need any customization : http.httpBasic(withDefaults());
        http.httpBasic(x -> x.authenticationEntryPoint(new CustomBasicAuthEntryPoint()));
        
        // Setting custom AuthenticationEntryPoint for handling authentication exceptions : 
        // http.exceptionHandling(x -> x.authenticationEntryPoint(new CustomBasicAuthEntryPoint()));
        
         // the denied user gonna be directed to ("/denied")
         // http.exceptionHandling(x -> x.accessDeniedHandler(new CustomAccessDeniedHandler()).accessDeniedPage("/denied"));
         
         http.exceptionHandling(x -> x.accessDeniedHandler(new CustomAccessDeniedHandler()));
        
        
        // without disabling csrf protection, we aren't gonna be allowed to register users
        http.csrf(x -> x.disable());
        return http.build();
    }
    
    /*@Bean
    public UserDetailsService userDetailsService(){
        UserDetails user = User.withUsername("user")
                //.password("{noop}user") (Compromised Password)
                .password("{bcrypt}$2a$10$Tp/D24wzyeObN2p8nyEhW.9U5jzsZxKp5pt7O0Oxk3mGdgl6dl8MC")
                .authorities("read").build();
        UserDetails admin = User.withUsername("admin")
                //.password("{noop}admin") (Compromised Password)
                //.password("{noop}AdminDONA@1234")
                .password("{bcrypt}$2a$10$/L3kJS1vT7L2wD.PYxiW1.timkFiAv2sE7Nfkt4r77BtZxLC0dYpi")
                .authorities("admin").build();
        return new InMemoryUserDetailsManager(user,admin);
    }
    */
    
    
    /*@Bean
    public UserDetailsService userDetailsService(DataSource datasource){
        return new JdbcUserDetailsManager(datasource);
    }
    */
    
    
    
    
    @Bean
    public PasswordEncoder endcoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    
    
    @Bean
    public CompromisedPasswordChecker passwordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
