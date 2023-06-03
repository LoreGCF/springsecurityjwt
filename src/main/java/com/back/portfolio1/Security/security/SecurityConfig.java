
package com.back.portfolio1.Security.security;

import com.back.portfolio1.Security.security.filters.JwtAuthenticationFilter;
import com.back.portfolio1.Security.security.filters.JwtAuthorizationFilter;
import com.back.portfolio1.Security.security.jwt.JwtUtils;
import com.back.portfolio1.Security.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    JwtUtils jwtUtils; 
    
    @Autowired
    UserDetailsServiceImpl userDetailsService; 
    
    @Autowired
    JwtAuthorizationFilter authorizationFilter;
    
    
    //1ºUn metodo que regule la cadena de filtros es decir la seguridad
    //con este metodo se configura la seguridad 
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager ) throws Exception{
        
        
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");
        
        return httpSecurity
        
//no vamos a trabajar con formularios por eso lo deshabilito, solo configuramos una autenticacion 
        .csrf(config ->config.disable())
                
//configuro el acceso a las URL y a los endpoint                
        .authorizeHttpRequests(auth ->{
            auth.requestMatchers("/hello").permitAll();//se permite acceso a todos los que ingresen a este endpoint
            auth.anyRequest().authenticated();//cualquier otra ruta debe estar autenticado el usuario para acceder
        
        })
        
                
        .sessionManagement(session ->{
//politica de creacion de la sesion
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        })
                
        .addFilter(jwtAuthenticationFilter)
        .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)

        .build();
    
    }
    

    
    
    
    
    
    
   
    @Bean
    PasswordEncoder passwordEncoder(){
    
        return new BCryptPasswordEncoder();
    
    }
    
    
    
    
    
    
    
   
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder) throws Exception{
    
        return httpSecurity
                .getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and().build();
    
    }
    
}
