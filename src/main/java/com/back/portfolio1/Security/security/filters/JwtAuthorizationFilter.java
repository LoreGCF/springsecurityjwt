
package com.back.portfolio1.Security.security.filters;

import com.back.portfolio1.Security.security.jwt.JwtUtils;
import com.back.portfolio1.Security.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

//OncePerRequest una vez por peticion, quiere decir que siempre tenemos que enviar el token de acceso por cada request 

@Component //se lo puede definir como component porque no necesito enviarle ningun parametro para que pueda funcionar
public class JwtAuthorizationFilter extends OncePerRequestFilter{

    @Autowired
    private JwtUtils jwtUtils;//porque necesito autenticar el token 
    
    @Autowired
    private UserDetailsServiceImpl userDetailsService;//para traer la informacion de la base de datos
            
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        //tengo que extraer el token de la peticion 
        String tokenHeader = request.getHeader("Authorization");
        
        if(tokenHeader != null && tokenHeader.startsWith("Bearer ")){
  
            
            
  //******        
               String token = tokenHeader.substring(7, tokenHeader.length());
            
               
               
            if(jwtUtils.isTokenValid(token)){
                String username = jwtUtils.getUsernameFronToken(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authenticationToken = 
                        new UsernamePasswordAuthenticationToken(username, null, userDetails.getAuthorities());
                
                //es el que contiene la autenticacion propia en la aplicacion
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            
            
            }
        
        
        
        }
        
        
        
        filterChain.doFilter(request, response);
        
        
        
    }
   
    
    
}
