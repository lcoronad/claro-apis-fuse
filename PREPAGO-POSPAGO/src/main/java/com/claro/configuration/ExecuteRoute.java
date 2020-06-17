package com.claro.configuration;

import java.util.Random;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.claro.utils.ConstantUtil;

@Service
public class ExecuteRoute {

	private Logger logger = LoggerFactory.getLogger(ExecuteRoute.class);

	@EndpointInject(uri = ConstantUtil.ROUTE_REFRESH_CACHE)
	private ProducerTemplate producerT;

	public void execute() {

		try {
			producerT.sendBodyAndHeader("", "test", new Random().nextBoolean());
		} catch (Exception e) {
			logger.error("Error al ejecutar tarea", e);
		}

	}

}
