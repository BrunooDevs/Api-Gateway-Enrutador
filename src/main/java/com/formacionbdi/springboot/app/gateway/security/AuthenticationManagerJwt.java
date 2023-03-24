package com.formacionbdi.springboot.app.gateway.security;

import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import reactor.core.publisher.Mono;

/*esta clase se usa para el proceso de autenticacion con el token jwt tambien valida su firma que sea correcta 
 * validadr el token en general , si todo sale bien nos autenticamos para poder acceder a lso recursos y rutas 
 * protegidas */

@Component
public class AuthenticationManagerJwt implements ReactiveAuthenticationManager{
	
	@Value("${config.security.oauth.jwt.key}")
	private String llaveJwt;

	@SuppressWarnings("unchecked")
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		/*por argumento recibimos el autentication que va a contener el token jwt,y ese autentication se va a pasar
		 * por en el filtro, y ademas lo vamos a validar con la firma*/

		/*Mono.just: convertimos un objeto a un objeto reactivo que se aparte de este flujo 
		 * getCredentials(): es el token jwt y lo convertimos a un string con toString()
		 * NOTA: ESTE TOKEN LO ESTA PASANDO VIA ARGUMENTO AUTENTICATE EL FILTRO*/
		return Mono.just(authentication.getCredentials().toString())
				/*map*/
				.map(token -> {
					/* Keys.hmacShaKeyFor: recibe un byte , por lo tanto convertimos el llaveJwt primero a base64
					 * con el metodo Base64.getEncoder().encode y despues con Keys.hmacShaKey lo convertimos a bytes*/
					SecretKey llave = Keys.hmacShaKeyFor(Base64.getEncoder().encode(llaveJwt.getBytes()));
					
					/*parserBuilder(): validamos la llave y devolvemos al flujo reactivo los claims que serian los 
					 * datos del token
					 * getBody(): obtenemos los claims, los datos del flujo
					  
					 *NOTA: EL FLUJO EMPESO CON EL token DE TIPO STRING  QUE SE PASA AL METODO MAP Y EL FLUJO SE 
					 *CONVIERTE SE DEVUELVE A UN TIPO CLEIMS */
					return Jwts.parserBuilder().setSigningKey(llave).build().parseClaimsJws(token).getBody();
				})
				.map(claims -> {
					/*extraemos los datos del token*/
					String username = claims.get("user_name", String.class);
					
					List<String> roles = claims.get("authorities", List.class);
					
					/*role -> new SimpleGrantedAuthority(role) se puede abreviar con la forma del metodo de referencia a 
					 SimpleGrantedAuthority::new
					 * */
					Collection<GrantedAuthority> authorities = roles.stream().
							 map(SimpleGrantedAuthority::new)
							.collect(Collectors.toList());
					
					return new UsernamePasswordAuthenticationToken(username,null,authorities );
				});
				
				/*por detras de escena se invoca al metodo suscribe lo hace de forma automatica no lo tenemos que hacer
				 * nosotros*/
				
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
