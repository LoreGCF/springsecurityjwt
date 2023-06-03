
package com.back.portfolio1.Security.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import java.util.Date;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;



/*Esta clase nos provee los metodos necesarios para trabajar con el token*/

@Component//va a ser un componente administrado por sprint
@Slf4j
public class JwtUtils {
    
    
    @Value("${jwt.secret.key}")
    private String secretKey;//nos ayuda a firmar nuestro metodo
    
    
    @Value("${jwt.time.expiration}")
    private String timeExpiration;
    
    
    /*Crear un metodo el cual se va a encargar de generar un token de acceso y lo codifica */
    public String generateAccesToken(String username){
        return Jwts.builder()
                .setSubject(username)
                
                //la fecha de creacion del toquen
                .setIssuedAt(new Date(System.currentTimeMillis()))
                //tiempo de expiracion del token
                //como el tiempo de expiracion es una string con long.parselong lo convertimos
                .setExpiration(new Date(System.currentTimeMillis() + Long.parseLong(timeExpiration)))
                //necesitamos obtener fima del token 2º
                .signWith(getSignatureKey(), SignatureAlgorithm.HS256)
                .compact();
    
        
    
    }
    
    /*Validar el toke de acceso*/
    public boolean isTokenValid(String token){
        try{
            //el parserBuilder hace lo contrario a codificar, lo decodifica 
            Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        //si suelta esto una excepcion es porque el token es invalido
        return true;
        
        }catch (Exception e){
            log.error("Token invalido, error: ".concat(e.getMessage()));
            return false; 
        
        }
    
    
    }
    
    
    //si quiero obtener el username del token
    public String getUsernameFronToken(String token){
        return getClaim(token, Claims::getSubject);
    
    }
    
    
    
    
    
    
    //para obtener un solo claim. Para hacerlo generico usamos el <T>
    public <T> T getClaim(String token, Function<Claims, T> claimsTFunction){
        Claims claims = extractAllClaims(token);
        
        //cual es el claim que necesito tener 
        return claimsTFunction.apply(claims); 
    
    }
    
    
    
    
    //cargar todos los claims que vienen dentro del token, se recupera la informacion.
    //se obtiene el listado con todos los claims 
    public Claims extractAllClaims(String token){
        
        return Jwts.parserBuilder()
                    .setSigningKey(getSignatureKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    
    
    
    }
    
    
    
    
    
    //Obtener firma del token 2º. Una firma encriptada que en el signwith se encripta aun mas
    
    public Key getSignatureKey(){
    
        byte[] KeyBytes = Decoders.BASE64.decode(secretKey);
        
        return Keys.hmacShaKeyFor(KeyBytes);
                
                
    
    
    }
    
    
}
