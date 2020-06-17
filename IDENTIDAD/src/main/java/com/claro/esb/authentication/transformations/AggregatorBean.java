package com.claro.esb.authentication.transformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

@Component
public class AggregatorBean implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {

		if (oldExchange == null) {
			newExchange.setProperty("results", new ArrayList<HashMap<String, String>>());
			return newExchange;
		} else {
			ArrayList<Map<String, Object>> listResults = oldExchange.getProperty("results", ArrayList.class);
			Map<String, Object> results = new HashMap<>();
			if (oldExchange.getIn().getHeader("resultMinAuth") == null
					&& newExchange.getIn().getHeader("resultMinAuth") != null) {
				results.put("resultMinAuth", newExchange.getIn().getHeader("resultMinAuth"));
			}

			if (oldExchange.getIn().getHeader("resultIdAuth") == null
					&& newExchange.getIn().getHeader("resultIdAuth") != null) {
				results.put("resultIdAuth", newExchange.getIn().getHeader("resultIdAuth"));
			}

			if (oldExchange.getIn().getHeader("resultAccountAuth") == null
					&& newExchange.getIn().getHeader("resultAccountAuth") != null) {
				results.put("resultAccountAuth", newExchange.getIn().getHeader("resultAccountAuth"));
			}

			if (oldExchange.getIn().getHeader("resultUserPassAuth") == null
					&& newExchange.getIn().getHeader("resultUserPassAuth") != null) {
				results.put("resultUserPassAuth", newExchange.getIn().getHeader("resultUserPassAuth"));
			}

			if (!results.isEmpty()) {
				listResults.add(results);
			}
			oldExchange.getIn().setBody(listResults);

			return oldExchange;
		}
	}

}
