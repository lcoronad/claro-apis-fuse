package com.claro.process;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.claro.dto.DetalleError;

import com.claro.utils.ConstantUtil;

public class ErrorProcessor implements Processor {

	private Logger logger = LoggerFactory.getLogger(ErrorProcessor.class);

	@Override
	public void process(Exchange exchange) throws Exception {
		String exception = exchange.getProperty(ConstantUtil.EXCEPTION, String.class);
		logger.info("Mensaje exception:{}", exception);
		Matcher matcher = Pattern.compile("\\[([^\\]]+)").matcher(exception);
		int pos = -1;
		List<String> tags = new ArrayList<>();
		while (matcher.find(pos + 1)) {
			pos = matcher.start();
			tags.add(matcher.group(1));
		}
		String[] datosArray = tags.get(0).split(";");
		List<DetalleError> detallesError = new ArrayList<>();
		DetalleError detalleError = new DetalleError();
		logger.debug("Datos capturados:{}", tags.get(0));
		for (int i = 1; i <= datosArray.length; i++) {
			String[] data = datosArray[i - 1].split(":");
			if (data.length == 2) {
				data[0] = data[0].trim();
				if (data[0].equalsIgnoreCase("property")) {
					detalleError.campo = data[1].trim();
				}
				if (data[0].equals("value")) {
					detalleError.valor = data[1].trim();
				}
				if (data[0].equals("constraint")) {
					detalleError.mensaje = new String(data[1].trim().getBytes(StandardCharsets.ISO_8859_1),
							StandardCharsets.UTF_8);
				}
			}
			if ((i % 3) == 0) {
				detallesError.add(detalleError);
				detalleError = new DetalleError();
			}
		}
		detallesError.forEach(item -> logger.debug("Datos error:{}-{}-{}", item.campo, item.mensaje, item.valor));
		exchange.setProperty(ConstantUtil.LIST_ERROR, detallesError);
	}

}
