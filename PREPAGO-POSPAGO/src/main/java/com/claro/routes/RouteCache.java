package com.claro.routes;



import java.io.Serializable;
import java.util.concurrent.TimeUnit;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.ehcache.EhcacheConstants;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.claro.utils.ConstantUtil;

@Component
public class RouteCache extends RouteBuilder{
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void configure() throws Exception {
		
		
		from(ConstantUtil.ROUTE_LOAD_CACHE).routeId("ROUTE_LOAD_CACHE").streamCaching()
			.errorHandler(noErrorHandler())
			.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio la ruta consulta cache")
			.setHeader(EhcacheConstants.ACTION, constant(EhcacheConstants.ACTION_GET))
	        .setHeader(EhcacheConstants.KEY, constant(123456789))
	        .to("ehcache:dataServiceCache?keyType=java.lang.Long&valueType=java.io.Serializable")
	        .choice()
            	.when(header(EhcacheConstants.ACTION_HAS_RESULT).isNotEqualTo("true"))
            		.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje:Inicio a consultar ITEL")
	                .setHeader(EhcacheConstants.ACTION, constant(EhcacheConstants.ACTION_PUT))
	                .setHeader(EhcacheConstants.KEY, constant(123456789))
	                .to(ConstantUtil.ROUTE_CONSULTA_CATALOGO_ITEL)
	                .to("ehcache:dataServiceCache?keyType=java.lang.Long&valueType=java.io.Serializable")
	                .log(LoggingLevel.INFO, logger, "Proceso: ${exchangeId} | Mensaje: Finalizo guardado de datos en cache")
                .otherwise()
                	.log(LoggingLevel.INFO, logger, "Proceso: ${exchangeId} | Retorna datos cargados en cache")
                .endChoice()
			.end();
		
		from(ConstantUtil.ROUTE_REFRESH_CACHE).routeId("ROUTE_REFRESH_CACHE").streamCaching()
				.onException(Exception.class)
					.handled(true)
					.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Error al refrescar la cache | Causa: ${exception.message}")
					.end()
				.setProperty(ConstantUtil.PROCESO_ID, simple("${exchangeId}"))
				.log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Inicio a refrescar la cache")
				.setHeader(EhcacheConstants.ACTION, constant(EhcacheConstants.ACTION_PUT))
	            .setHeader(EhcacheConstants.KEY, constant(123456789))
	            .to(ConstantUtil.ROUTE_CONSULTA_CATALOGO_ITEL)
                .to("ehcache:dataServiceCache?keyType=java.lang.Long&valueType=java.io.Serializable")
                .log(LoggingLevel.ERROR, logger, "Proceso: ${exchangeProperty.procesoId} | Mensaje: Finalizo guardado en cache")
			.end();
		
	}
	
	@Bean
	public CacheConfiguration<Long, Serializable> cacheConfiguration(){
		logger.info("Inyecto el bean de cache");
		return CacheConfigurationBuilder.newCacheConfigurationBuilder(Long.class, Serializable.class,
		        ResourcePoolsBuilder.heap(100)) 
		    .withExpiry(Expirations.timeToLiveExpiration(Duration.of(3000, TimeUnit.SECONDS))) 
		    .build();
	}

}
