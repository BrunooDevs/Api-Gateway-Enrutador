package com.formacionbdi.springboot.app.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;



import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter{
	
	@Autowired
	private ReactiveAuthenticationManager authenticationManager;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

		
		/*just: convertimos un objeto que va hacer el token con el bearer el token de la cabeza Authorization 
		 * que enviamos desde una aplicacion , lo convertimos a un flujo*/
		return Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
				
				/*.filter: preguntamos si la cabecera empiesa con bearer si existe continuamos con el flujo con ese
				 * elemento*/
				.filter(oauthHeader -> oauthHeader.startsWith("Bearer "))
				
				/*switchIfEmpty: si en la cabecera no biene Bearer continuamos con un token vacio y nos salimos del
				 flujo*/
				.switchIfEmpty(chain.filter(exchange).then(Mono.empty()))
				
				/*si el token contiene la palabra Bearer quitamos la palabra bearer del token, recordemos que map lo
				  que hace es tranformar el flujo (NOTA: NECESITAMOS SOLO EL TOKEN SIN LA PALABRA Bearer)*/
				.map(token -> token.replace("Bearer ", ""))
				
				/*aqui necesitamos el authentication manager lo que implementamos en la clase AuthenticationManagerJwt 
				 * lo debemos de injectar en esta clase, ademas pasamos new UsernamePasswordAuthenticationToken con el
				 * token le pasamos el rol l atarea de procesar el token de validarlo  y autenticanos al otro componente
				 * al authenticationManager que estamos injectando*/
				.flatMap(token -> authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(null,token)))
				
				/*flatmap: es muy parecido al map pero map devuelve al flujo un objeto comun y coriente mientras flatmap
				 * devuelve otro flujo como por jemeplo un mono o un flux,
				 * en este flatmap se emite el objeto authentication que pasamos en la parte de arriba (.flatMap(token -> authenticationManage)
				 * chain.filter(): nos autenticamos en el contexto, pasamos el exchange en el metodo para seguir con la
				 * ejecucion de los demas filtro y del request y con contextWrite(ReactiveSecurityContextHolder.withAuthentication( 
				 * asignamos el objeto autentication al contexto de spring security y al contexto de nuestra aplicacion*/
				 
				.flatMap(authentication -> chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
				;
	}
	
	
	
	
	
	
	
	
	
	

}
