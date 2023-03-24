package com.formacionbdi.springboot.app.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/*@EnableWebFluxSecurity : habilitamos la seguridad en webflux es una clase que no implementa nada simplemente con esta anotacion que 
 * es de configuracion vamos a obtener un metodo bean que ba a registrar un componente*/

@EnableWebFluxSecurity
public class SpringSecurityConfig {
	
	/*wtAuthenticationFilter es el filtro que realizamos en la clase JwtAuthenticationFilter */
	@Autowired 
	private JwtAuthenticationFilter authenticationFilter;
	
	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) {
	
		return http.authorizeExchange()
				
				/*agregamos la ruta /api/security/oauth/**" con acceso publico*/
				.pathMatchers("/api/security/oauth/**").permitAll()
				.pathMatchers(HttpMethod.GET,"/api/productos/listar",
						"/api/items/listar",
						"/api/usuarios/usuarios",
						"/api/items/ver/{id}/cantidad/{cantidad}",
						"/api/productos/ver/{id}").permitAll()
						
				.pathMatchers(HttpMethod.GET,"/api/usuarios/usuarios/{id}").hasAnyRole("ADMIN", "USER")
						
				/*estas rutas son privadas para los admid*/
				.pathMatchers("/api/productos/**","/api/items/**","/api/usuarios/usuarios/**").hasAnyRole("ADMIN")
				 
				.anyExchange()
				/*protegemos todas nuestros endpoints*/
				.authenticated()
				
				/*desactivamos el token csrf que son para vistas de formulario como por ejemplo tulizando thymeleaf 
				 * o jsp NOTA: EN NUESTRO CASO ES UNA API REST UN MICROSERVICIO NO NECESITAMOS ESE TOKEN NI ESA 
				 * SEGURIDAD*/
				.and()
				
				/*registrmoas el filtro que estamos injectando en esta clase que es el de la clase 
				 * JwtAuthenticationFilter, el primer parametro es el filtro y el segundo el orden la posiscion*/
				.addFilterAt(authenticationFilter,SecurityWebFiltersOrder.AUTHENTICATION)
				
				
				.csrf().disable()
				.build();
				
				
	}

}
