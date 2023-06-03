
package com.back.portfolio1.Security.security.filters;

import com.back.portfolio1.Security.models.UserEntity;
import com.back.portfolio1.Security.security.jwt.JwtUtils;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


//el authenticationfilter nos ayuda a autenticarnos en la aplicacion 
//para que esta clase tenga efecto tengo que configurarla en configSecurity
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private JwtUtils jwtUtils;
    
    public JwtAuthenticationFilter(JwtUtils jwtUtils){
        this.jwtUtils = jwtUtils; 
    
    
    }
    
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, 
                                                HttpServletResponse response) throws AuthenticationException {
        
        UserEntity userEntity = null;
        String username = "";//para recuperar estos valores
        String password = ""; 
        
        try{
        userEntity = new ObjectMapper().readValue(request.getInputStream(), UserEntity.class);
        username = userEntity.getUsername();
        password = userEntity.getPassword();
        
        
        
        /***************************************/
        //esto es diferente 
         } catch (StreamReadException e) {
            throw new RuntimeException(e);
        } catch (DatabindException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        //Con esto nos estaríamos autenticando en la aplicacion
        UsernamePasswordAuthenticationToken authenticationToken = 
                new UsernamePasswordAuthenticationToken(username, password); 
        
        
        return getAuthenticationManager().authenticate(authenticationToken); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
    
    
    //si la autenticacion anterior es correcta se genera el token
    
    @Override
    protected void successfulAuthentication(HttpServletRequest request, 
                                           HttpServletResponse response, 
                                           FilterChain chain, 
                                           Authentication authResult) 
            throws IOException, ServletException {
        
        User user =  (User) authResult.getPrincipal(); //obtenemos el objeto que contiene todos los detalles del usuario
        
        /*luego de obtener los datos del usuario tenemos que generar el token de acceso*/
        String token = jwtUtils.generateAccesToken(user.getUsername());
        
        
        //en el header de la respuesta vamos a enviar el token
        response.addHeader("Authorization",  token);
        
        Map<String, Object> httpResponse = new HashMap<>();
        httpResponse.put("token", token);
        httpResponse.put("Message","Autenticacion correcta");
        httpResponse.put("Username", user.getUsername());
        
        //necesitamos pasar este archivo a json 
        //response. ahi estariamos escribiendo el mapa como json en la respuesta 
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(httpResponse));
        response.setStatus(200);//response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);//"application/json" tambien se podria colocar en lugar de appication_json_value
        response.getWriter().flush();//nos asegura que todo se escriba correctamente
        
        super.successfulAuthentication(request, response, chain, authResult); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
    }
    
    
    
    
}
