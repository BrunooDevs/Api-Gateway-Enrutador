package com.formacionbdi.springboot.app.gateway.filters;


import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;



import reactor.core.publisher.Mono;

@Component
public class EjemploGlobalFilter implements GlobalFilter, Ordered{

	private static final Logger  logger = LoggerFactory.getLogger(EjemploGlobalFilter.class);
	
	
	/*para poder implementar todoas estas clases dentro de spring cloud gateway, recordemos de que por detras
	 * de escena esta trabajando con webflux programacion reactiva
	  
	 *  exchange: podemos acceder al request y al response y de alguna forma modificarlos o realizar 
	    validaciones y segun los parametros que enviemos, las  cabeceras , parametros del request, si se
	    cumplen cierta condicion podriamos permitir o rechazar el acceso a nuestro microservicio  }
	    
	     el metodo filter devuelve un objeto Mono<Void>
	    */
	
	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		
		logger.info("ejecutando filtro pre");
		
		
		/*siempre el request debe de ir en el pre antes de que se envie al microservicio
		 *exchange.getRequest().mutate().headers(h -> h.add("token","123456")); pasamos la cabecera al 
		 *request (se creara una cabecera llamada token con valor 123456) */
		exchange.getRequest().mutate().headers(h -> h.add("token","123456"));
		
		
		
		
		/*todo lo que este por sobre el return chain.filter(exchange); sera el pre*/
		
		/*chain.filter: continua con la ejecucion de los demas filters se van invocando en cadena, podriamos
		 * tener mas filtros, hata ejecutar el request a nuestro microservicio y despues dentro de filter,
		 * podemos manejar una expresio landa y dentro de esa expresion landa manejamos la respuesta (post)
		 * 
		 then: se ejecuta despues cuando aya finalizado toda la ejecucion de todos los filtros y aya finalizado
		 el proceso y ayamos obtenido la respuesta, dentro de then manejamos todo el post
		 
		 Mono.fromRunnable: lo pasamos por argumento en el then, nos permite crear un objeto reactivo un mono
		 el metodo filter devuelve un objeto Mono<Void> entonces dentro de Mono.fromRunnable creamos este
		 objeto reactivo para implementar la tarea el "post"
		 * */

		return chain.filter(exchange).then(Mono.fromRunnable(() -> {
			logger.info("ejecutando filtro post");
			
			
			/*obtenemos el valor que nos envia el request desde el pre y se lo asignmamos al response
			 pasamos el valor de la cookie a un objeto optional 
			* ofNullable: of es para convertir a valor a un optional y Nullable si el valor es nulo o sino
			  contiene valor esta variable simplemente va a pasar o guardar un optional vacio (empty)
			* ifPresent() preguntamos si existe el valor, y me diante una expreasion landa asignar el valor 
			  si esta presente 
			 
			 * NOTA: ESTE VALOR LO PODRIAMOS OBTENER DESDE CUALQUIER CONTROLADOR DE CUALQUIER MICROSERVICIO*/
			Optional.ofNullable(exchange.getRequest().getHeaders().getFirst("token")).ifPresent(valor->{
				exchange.getResponse().getHeaders().add("token",valor);
			});
			
			
			/*modificamos la respuesta la cabecera cookie le asignamos el valor de color igual a rojo*/
			 exchange.getResponse().getCookies().add("color", ResponseCookie.from("color", "rojo").build());
			
			/*modificamos la respuesta, nuestro microservicio nos retorna un JSON y la cambiamos a un texto
			 * plano, text/plain*/
			
			//exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_PLAIN);
	
		}));
	}


	/*este metodo lo que hace es que podemos ordenar la ejecuino de los filtros dependiendo de como queramos
	 * que se apliquen*/
	
	@Override
	public int getOrder() {
		// TODO Auto-generated method stub
		return 100;
	}
	
	
	

}
