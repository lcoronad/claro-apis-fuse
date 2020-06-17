package com.claro.process;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BuildDatesProcesor implements Processor {



	private Logger logger = LoggerFactory.getLogger(BuildDatesProcesor.class);
	
	private static final String DATE_ZONE = "America/Bogota";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String PROCESO_ID = "procesoId";

	@Override
	public void process(Exchange exchange) throws Exception {


		ZonedDateTime now = ZonedDateTime.now(ZoneId.of(DATE_ZONE));
		ZonedDateTime endStart = now.toLocalDate().atStartOfDay(ZoneId.of(DATE_ZONE));
		endStart = endStart.plusSeconds(-1);
		ZonedDateTime startDate = endStart.toLocalDate().atStartOfDay(ZoneId.of(DATE_ZONE)).plusDays(-(endStart.getDayOfMonth() - 1));
		String endDate = DateTimeFormatter.ofPattern(DATE_FORMAT).format(endStart);
		String startDateT = DateTimeFormatter.ofPattern(DATE_FORMAT).format(startDate.toLocalDate().atStartOfDay());
		logger.info("Proceso={} | Fecha inicio zone:{}",exchange.getProperty(PROCESO_ID),now);
		logger.info("Proceso={} | Fecha formato final:{}",exchange.getProperty(PROCESO_ID), endDate);
		logger.info("Proceso={} | Fecha formato inicio:{}",exchange.getProperty(PROCESO_ID) ,startDateT);
		logger.info("Proceso:{} | Días a Restar:{}",exchange.getProperty(PROCESO_ID),endStart.getDayOfMonth());
		exchange.getIn().setHeader("startDate", startDateT);
		exchange.getIn().setHeader("endDate", endDate);
		exchange.getIn().setHeader("month", startDate.getMonthValue());

	}

}
