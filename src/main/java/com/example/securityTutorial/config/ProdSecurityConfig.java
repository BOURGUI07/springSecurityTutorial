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
@Profile("prod")
public class ProdSecurityConfig {
    
    
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        // handle Invalid Sessions
        // Redirect User to another page ("/invalidSession)
        // Restrict User to Have ONLY ONE Session!
        http.sessionManagement(s->s.invalidSessionUrl("/invalidSession").maximumSessions(1).maxSessionsPreventsLogin(true));
        
        // Accepting Only HTTPS protocol requests
        http.requiresChannel(x -> x.anyRequest().requiresSecure());
        
        
        // http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        // http.authorizeHttpRequests(requests -> requests.anyRequest().denyAll());
        http.authorizeHttpRequests(requests -> requests
                .requestMatchers("/myAccount", "/myBalance","/myLoans", "/myCards").authenticated()
                .requestMatchers("/notices", "/contact","/error", "/register","/invalidSession").permitAll()
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
