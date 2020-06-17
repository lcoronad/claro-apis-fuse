package com.claro;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import com.claro.dto.Data;
import com.claro.dto.DataAprovisionamiento;
import com.claro.dto.DataCatalogo;
import com.claro.dto.Request;
import com.claro.dto.RequestAprovisionamiento;
import com.claro.dto.RequestCatalogo;


/**
 * 
 * @author Assert Solutions S.A.S
 *
 */
@RunWith(SpringRunner.class)
@Configuration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, properties = { "server.port=8081" })
public class ApplicationTest {

    @Value("${server.port}")
    private String serverPort;

    @Autowired
    private TestRestTemplate restTemplate;
    
    private static final String HOST_URL = "http://localhost:8081/appirappi/" ;
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Test
    public void testGet() throws Exception {

        // Call the REST API
        ResponseEntity<String> response = restTemplate.getForEntity("http://localhost:8081/api/hello-service",
                String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().contains("Hello"));
    }

    @Test
    public void testErrorConsultaFactura() throws Exception {

        // Call the REST API
    	Data data = new Data();
    	data.canal = "1";
//    	data.idReferencia = "1352956834";
    	data.min = "3127800091";
    	Request r = new Request();
    	r.setData(data);
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
    	headers.add("ClientID", "545454554ff");
    	headers.add("NombreCliente", "Rappi"); 
    	HttpEntity<Request> httpEntity = new HttpEntity<Request>(r, headers);
        ResponseEntity<String> response = restTemplate.exchange(HOST_URL + "consultaFactura/v1", HttpMethod.POST, httpEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimeUnit.SECONDS.sleep(10);
        logger.info("Termino el test:{}", response.getBody());
    } 
    
    @Test
    public void testConsultaFacturaMin() throws Exception {

        // Call the REST API
    	Data data = new Data();
    	data.canal = "1";
//    	data.idReferencia = "1352956834";
    	data.min = "3127800091";
    	data.bankCode = "";
    	data.paymentReceptionDate = "";
    	Request r = new Request();
    	r.setData(data);
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
    	headers.add("ClientID", "545454554ff");
    	headers.add("NombreCliente", "Rappi"); 
    	HttpEntity<Request> httpEntity = new HttpEntity<Request>(r, headers);
        ResponseEntity<String> response = restTemplate.exchange(HOST_URL + "consultaFactura/v1", HttpMethod.POST, httpEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimeUnit.SECONDS.sleep(10);
        logger.info("Termino el test:{}", response.getBody());
    }
    
    @Test
    public void testConsultaFacturaReferenceId() throws Exception {

        // Call the REST API 
    	Data data = new Data();
    	data.canal = "1";
    	data.idReferencia = "1352956834";
//    	data.min = "3127800091";
    	data.bankCode = "038";
    	data.paymentReceptionDate = "2019-12-16T11:56:10";
    	Request r = new Request();
    	r.setData(data);
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
    	headers.add("ClientID", "545454554ff");
    	headers.add("NombreCliente", "Rappi"); 
    	HttpEntity<Request> httpEntity = new HttpEntity<Request>(r, headers);
        ResponseEntity<String> response = restTemplate.exchange(HOST_URL + "consultaFactura/v1", HttpMethod.POST, httpEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TimeUnit.SECONDS.sleep(10);
        logger.info("Termino el test:{}", response.getBody());
    }
    
    @Test
    public void testCatalogo() throws InterruptedException {
    	DataCatalogo data = new DataCatalogo();
    	data.canal = "1";
    	data.min = "3142877111";
    	RequestCatalogo request = new RequestCatalogo();
    	request.data = data;
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
    	headers.add("ClientID", "54545455");
    	headers.add("NombreCliente", "Rappi"); 
    	HttpEntity<RequestCatalogo> httpEntity = new HttpEntity<RequestCatalogo>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(HOST_URL + "consultarPaquetes/v1", HttpMethod.POST, httpEntity, String.class);
        TimeUnit.SECONDS.sleep(5);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.info("Termino el test:{}", response.getBody());
    	
    }
    
    @Test
    public void testAprovisionamiento() {
   
    	DataAprovisionamiento data = new DataAprovisionamiento();
    	data.canal = "1";
    	data.min = "3139886622";
    	data.nombrePaquete = "datos y mensajes";
    	data.offerId = "77678";
    	RequestAprovisionamiento request = new RequestAprovisionamiento();
    	
    	request.data = data;
    	HttpHeaders headers = new HttpHeaders();
    	headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
    	headers.add("ClientID", "545454554ff");
    	headers.add("NombreCliente", "Rappi"); 
    	HttpEntity<RequestAprovisionamiento> httpEntity = new HttpEntity<RequestAprovisionamiento>(request, headers);
        ResponseEntity<String> response = restTemplate.exchange(HOST_URL + "adquirirPaquete/v1", HttpMethod.POST, httpEntity, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.info("Termino el test:{}", response.getBody());
    	
    }

}