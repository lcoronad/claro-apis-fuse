package com.claro.bean;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class GenerateFileBean {

	private Logger logger = LoggerFactory.getLogger(GenerateFileBean.class);

	@Autowired
	private Environment env;
	
	private static final String DATE_ZONE = "America/Bogota";
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Handler
	public ByteArrayOutputStream generarArchivo(Exchange exchange) {

		try (XSSFWorkbook workbook = new XSSFWorkbook();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			XSSFSheet sheet = workbook.createSheet("Auditoria");

			@SuppressWarnings("unchecked")
			List<Map<?, ?>> listaCompletaSPData = exchange.getIn().getBody(List.class);

			int[] rownum = { 0 };
			if (!listaCompletaSPData.isEmpty()) {

				// Create headers
				Row rowHeaders = sheet.createRow(0);
				Map<?, ?> headers = listaCompletaSPData.get(0);
				int[] i = { 0 };
				headers.forEach((k, v) -> {
					rowHeaders.createCell(i[0]++).setCellValue(String.valueOf(k));
				});

				if (env.getProperty("reporte.headers").equalsIgnoreCase("true")) {
					rownum[0] = 1;
				}

				// Create body excel
				listaCompletaSPData.forEach(mapa -> {
					Row row = sheet.createRow(rownum[0]++);
					int[] cellnum = { 0 };
					for (Entry<?, ?> value : mapa.entrySet()) {
						row.createCell(cellnum[0]++).setCellValue(String.valueOf(value.getValue()==null?"":value.getValue()));
					}
				});

				workbook.write(outputStream);

			}
			logger.info("Proceso= {} | Mensaje= Finalizo creacion del archivo",exchange.getProperty("procesoId"));
			return outputStream;
		} catch (Exception e) {
			logger.error("Error al crear el archivo", e);
		}
		return null;

	}
	
	public String localDate() {
		ZonedDateTime dateBogota= ZonedDateTime.now(ZoneId.of(DATE_ZONE));
		return DateTimeFormatter.ofPattern(DATE_FORMAT).format(dateBogota);
	}

}
