package com.back.portfolio1;

import com.back.portfolio1.Security.models.ERole;
import com.back.portfolio1.Security.models.RoleEntity;
import com.back.portfolio1.Security.models.UserEntity;
import com.back.portfolio1.Security.repositories.UserRepository;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Portfolio1Application {

	public static void main(String[] args) {
		SpringApplication.run(Portfolio1Application.class, args);
	}

        
        @Autowired
        PasswordEncoder passwordEncoder;
       
        @Autowired
        UserRepository userRepository; 
        
        
        @Bean
        CommandLineRunner init(){
        
        return args -> {
        
            UserEntity userEntity = UserEntity.builder()
                    .email("g_fgl@live.com.ar")
                    .username("lorena")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.ADMIN.name()))
                            .build()))
                    .build();
            
            UserEntity userEntity2 = UserEntity.builder()
                    .email("angy@live.com.ar")
                    .username("anyi")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.USER.name()))
                            .build()))
                    .build();
            
             UserEntity userEntity3 = UserEntity.builder()
                    .email("hugo@live.com.ar")
                    .username("hugo")
                    .password(passwordEncoder.encode("1234"))
                    .roles(Set.of(RoleEntity.builder()
                            .name(ERole.valueOf(ERole.INVITED.name()))
                            .build()))
                    .build();   
            
            
            userRepository.save(userEntity);
            userRepository.save(userEntity2);
            userRepository.save(userEntity3);
        
        };
        
        }        
        
        
}
