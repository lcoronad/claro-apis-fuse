package com.claro.configuration;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.claro.routes.GenerarReporte;

@Service
public class ExecuteRoute {

	private Logger log = LoggerFactory.getLogger(ExecuteRoute.class);
	
	@EndpointInject(uri = GenerarReporte.GENERA_REPORTE_ROUTE)
	ProducerTemplate producerTemplate;
	
	@Autowired
	private Environment env;

	public void execute() {
		log.info("Inicio a ejecutar la ruta");
		try {
			String[] headers = {env.getProperty("reporte.header.geolocalizacion"), env.getProperty("reporte.header.autenticacion"), env.getProperty("reporte.header.prepago.pospago")};
			log.info("template: {}" , producerTemplate);
			for (String item : headers) {
				
				Runnable task = ()->{
						log.info("api a procesar:{}",item);
						producerTemplate.sendBodyAndHeader("", "api", item);
					
				};
				new Thread(task).start();
				Thread.sleep(3000);
			}
		} catch (Exception e) {
			log.error("Error:{}" ,e.getMessage());
		}
	}

}
