package com.claro.beans;

import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.claro.dto.Auditoria;
import com.claro.utils.ConstantUtil;

@Component
public class ProxyBean {

	@Autowired
	private Environment env;

	@Handler
	public String consumeParadigma(Exchange exchange) throws  Exception {
		Auditoria auditoria = exchange.getProperty(ConstantUtil.AUDITORIA_PROPERTY, Auditoria.class);
		HttpHost proxy = new HttpHost(env.getProperty("proxy.url"), Integer.valueOf(env.getProperty("proxy.port")));
		DefaultProxyRoutePlanner defaultProxyRoutePlanner = new DefaultProxyRoutePlanner(proxy);
		CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY,
				new NTCredentials(env.getProperty("proxy.user"), env.getProperty("proxy.password"),
						env.getProperty("proxy.url") + ":" + env.getProperty("proxy.port"),
						env.getProperty("proxy.domain")));
		AuthCache authCache = new BasicAuthCache();
		BasicScheme basicScheme = new BasicScheme();
		authCache.put(proxy, basicScheme);
		HttpClientContext httpContext = HttpClientContext.create();
		httpContext.setCredentialsProvider(credentialsProvider);
		httpContext.setAuthCache(authCache);
		HttpClient httpClient = HttpClients.custom().setRoutePlanner(defaultProxyRoutePlanner)
				.setDefaultCredentialsProvider(credentialsProvider).build();
		HttpPost httpPost = new HttpPost(env.getProperty("paradigma.url"));
		StringEntity entity = new StringEntity(exchange.getIn().getBody(String.class));
		httpPost.setEntity(entity);
		httpPost.setHeader(Exchange.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
		HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);
		String body = EntityUtils.toString(httpResponse.getEntity());
		auditoria.codResponseSE = httpResponse.getStatusLine().toString();
		auditoria.msmResponseSE = body;
		exchange.setProperty(ConstantUtil.AUDITORIA_PROPERTY, auditoria);
		exchange.getIn().setHeader("statuscode",  httpResponse.getStatusLine().toString());
		
		return body;
	}

}
