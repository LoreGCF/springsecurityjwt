
package com.back.portfolio1.Security.service;

import com.back.portfolio1.Security.models.UserEntity;
import com.back.portfolio1.Security.repositories.UserRepository;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service//para que sea un objeto administrado por sprint
public class UserDetailsServiceImpl implements UserDetailsService {
    
    //implementamos nuestro user para que vaya y consulte el usuario a la base de datos 
    @Autowired 
    private UserRepository userRepository; 
    

    /*Este metodo es muy importante implementarlo porque le dice a spring security de donde va a 
    traer los usuarios*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       
        UserEntity userEntity = userRepository.findByUsername(username)
                                               .orElseThrow(() -> new UsernameNotFoundException("El usuario "+ username +" no existe." ));
        
        
        
        Collection<? extends GrantedAuthority> authorities = userEntity
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_".concat(role.getName().name())))
                .collect(Collectors.toSet());//dejar como set no como list porque la list permite duplicados
        
        
        return new User(userEntity.getUsername(),
        userEntity.getPassword(),
        true,
        true,
        true,
        true,
        authorities);
        
        
        
    }
    
    
    
    
}
