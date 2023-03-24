package com.formacionbdi.springboot.app.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*Para la ultima versión Spring boot versión 3.0.1 se debe usar la versión de Spring cloud 2022.0.0 y jdk 17
no admite versiones anteriores, ademas se deben quitar estos dos anotaciones en la clase del main //@EnableCircuitBreaker

//@EnableEurekaClient*/

@SpringBootApplication
public class SpringbootServicioGatewayServerApplication {
	
	
	/*API GATEWAY trabaja con programacion reactiva mientras zuul trabaja con el api servlet las
	 * aplicaciones reactivas no bloquean las peticiones los request sino que puede hacer peticiones
	 * asyncronas, spring cloud gateway como trabaja con programacion reactiva no es compatible con 
	 * spring security OAuth2 solo es compatible con el api servlet por lo tanto con zuul
	 * 
	 Cloud LoadBalancer ya se incruye de forma automatica y se pueden crear varias instancias de un 
	 microservicio sin incluir a ribbon
	 
	 
	 NOTA IMPORTANTE: PODEMOS GENERAR EL TOKEN CON EL MICROSERVICIO servicio-usuaiors-oaith2 pero la implementancion
	 para el servidor de recursos es diferente al que se aplica en zuul ya que GATEWAY utiliza programacion reactiva
	 por lo tanto la implementacion del servidor de recursos es diferente 
	 **/
	

	public static void main(String[] args) {
		SpringApplication.run(SpringbootServicioGatewayServerApplication.class, args);
	}

}
