package com.claro.routes;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.claro.bean.GenerateFileBean;
import com.claro.process.BuildDatesProcesor;

@Component
public class GenerarReporte extends RouteBuilder{
	
	private Logger logger = LoggerFactory.getLogger(GenerarReporte.class);
	
	public static final String GENERA_REPORTE_ROUTE = "direct://gerera-reporte";
	public static final String DIRECTORIO = "directorio";

	@Override
	public void configure() throws Exception {
		
		from(GENERA_REPORTE_ROUTE).routeId("GeneraReporteRoute").streamCaching("true")
			.errorHandler( noErrorHandler())
			.choice()
				.when(simple("${exchangeProperty.procesoId} == null || ${exchangeProperty.procesoId} == ''"))
					.setProperty("procesoId", simple("${exchangeId}"))
				.endChoice()
			.end()
			.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= Inicio a consultar la DB Detalle = {{reporte.connection.user}}")
			.process(new BuildDatesProcesor())
			.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= Fechas a consultar | Detalle = {fechaInicio=${headers.startDate} , fechaFin=${headers.endDate}}")
			.setProperty("api_name_geoloc", simple("{{reporte.header.geolocalizacion}}"))
			.setProperty("api_name_auth", simple("{{reporte.header.autenticacion}}"))
			.setProperty("api_name_prepago_pospago", simple("{{reporte.header.prepago.pospago}}"))
			.choice()
				.when(simple("${headers.api} == ${exchangeProperty.api_name_geoloc}"))
					.setHeader(DIRECTORIO, simple("{{reporte.ftp.directorio.localizacion}}"))
					.log(LoggingLevel.DEBUG, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= {{reporte.sql}}")
					.to("sql:{{reporte.sql}}?dataSource=dataSource")
				.endChoice()
				.when(simple(" ${headers.api} == ${exchangeProperty.api_name_auth} "))
					.setHeader(DIRECTORIO, simple("{{reporte.ftp.directorio.autenticacion}}"))
					.log(LoggingLevel.DEBUG, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= {{reporte.sql.api.auth}}")
					.to("sql:{{reporte.sql.api.auth}}?dataSource=dataSource")
				.endChoice()
				.when(simple(" ${headers.api} == ${exchangeProperty.api_name_prepago_pospago} "))
					.setHeader(DIRECTORIO, simple("{{reporte.ftp.directorio.prepago.pospago}}"))
					.log(LoggingLevel.DEBUG, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= {{reporte.ftp.directorio.prepago.pospago}}")
					.to("sql:{{reporte.sql.api.prepago.pospago}}?dataSource=dataSource")
				.endChoice()
				.otherwise()
					.throwException(Exception.class, "No hay coincidencias")
				.end()
			.end()
			.log(LoggingLevel.DEBUG, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= Información obtenida | ${body.size()}")
			.bean(GenerateFileBean.class)
			.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= Archivo generado correctamente ")
			.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= Conectandose el servidor sftp | Detalle: Carpeta ${headers.directorio}")
			.setHeader("fechaLocal").method("generateFileBean", "localDate")
//			.toD("ftps:elkin@localhost:25/?password=123456&localWorkDirectory=/${headers.directorio}&passiveMode=true&delay=2000&disableSecureDataChannelDefaults=true&fileName={{reporte.nombre.archivo}}_${headers.api}.xlsx")
			.toD("sftp:{{reporte.ftp.host}}/${headers.directorio}?strictHostKeyChecking=true&useUserKnownHostsFile=yes&username={{reporte.ftp.user}}&password={{reporte.ftp.password}}&fileName={{reporte.nombre.archivo}}_${headers.api}_${headers.month}.xlsx")
			.log(LoggingLevel.INFO, logger, "Proceso= ${exchangeProperty.procesoId} | Mensaje= Archivo enviado al sftp | Detalle= Nombre del archivo ({{reporte.nombre.archivo}}_${headers.api}_${headers.month}.xlsx) ")
			.end();
		
		
	}

}
