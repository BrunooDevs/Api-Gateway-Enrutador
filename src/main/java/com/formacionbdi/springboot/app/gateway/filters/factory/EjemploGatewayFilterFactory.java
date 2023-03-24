package com.formacionbdi.springboot.app.gateway.filters.factory;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;
import java.util.Optional;

/*NOTA: ES IMPORTANTE QUE ESTA CLASE TENGA EL SUFIJO GatewayFilterFactory PARA QUE SPRING LO PUEDA DETECTAR
        EL NOMBRE DE FORMA AUTOMATICA*/

/*AbstractGatewayFilterFactory<C> es una clase generica que es la clase de configuracion, por ejemplo los 
 * parametros que vamos a pasar al filtro*/

@Component
public class EjemploGatewayFilterFactory extends AbstractGatewayFilterFactory<EjemploGatewayFilterFactory.Configuracion>{

	private static final Logger  logger = LoggerFactory.getLogger(EjemploGatewayFilterFactory.class);
	
	
	public EjemploGatewayFilterFactory() {
		
		/*en el metodo super pasamos la clase de configuracion*/
		super(Configuracion.class);
	}


	/*aplicar el filtro con nuestra configuracion, el parametro Configuracion config es la clase de 
	 * configuracion que definimos abajo*/
	
	@Override
	public GatewayFilter apply(Configuracion config) {

		return (exchange, chain) -> {
			
			logger.info("ejecutando pre gateway filter factory" + config.mensaje);
			/*todo lo que este arriba de aqui es el pre*/
			
			
			
			/*todo loque este despues del return chain.filter(exchange).then(Mono.fromRunnable(() es el post*/
			return chain.filter(exchange).then(Mono.fromRunnable(() ->{
				
				/*preguntamos si el valor de la cookie es distinto de nulo o podemos 
				 * 
				 obtenemos el valor que nos envia el request desde el pre y se lo asignmamos al response
			     pasamos el valor de la cookie a un objeto optional 
			     ofNullable: of es para convertir a valor a un optional y Nullable si el valor es nulo o sino
			     contiene valor esta variable simplemente va a pasar o guardar un optional vacio (empty)
			     ifPresent() preguntamos si existe el valor, y me diante una expreasion landa asignar el valor 
			     si esta presente  sino no hace nada
			     build(): genera ese objeto
			     */
				
				Optional.ofNullable(config.cookieValor).ifPresent(cookie->{
					exchange.getResponse().addCookie(ResponseCookie.from(config.cookieNombre,cookie).build());
				});
				
				logger.info("ejecutando post gateway filter factory: " + config.mensaje);
						
			}));
		};
	}
	
	
	
	/*esto es una clase  de Configuracion*/
	public static class Configuracion {
		private String mensaje;
		private String cookieValor;
		private String cookieNombre;
		public String getMensaje() {
			return mensaje;
		}
		public void setMensaje(String mensaje) {
			this.mensaje = mensaje;
		}
		public String getCookieValor() {
			return cookieValor;
		}
		public void setCookieValor(String cookieValor) {
			this.cookieValor = cookieValor;
		}
		public String getCookieNombre() {
			return cookieNombre;
		}
		public void setCookieNombre(String cookieNombre) {
			this.cookieNombre = cookieNombre;
		}
		
		
		
	}
	
	

}
