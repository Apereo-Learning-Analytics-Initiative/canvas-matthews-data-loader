package unicon.matthews.dataloader.canvas;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.client.RestTemplate;

import unicon.matthews.dataloader.MatthewsClient;

@SpringBootApplication
public class App {
  
  @Value("${matthews.baseurl:http://localhost:9966}")
  private String matthewsBaseUrl;
  
  @Value("${matthews.apikey}")
  private String matthewsApiKey;
  
  @Value("${matthews.apisecret}")
  private String matthewsApiSecret;
  
  public static void main(String[] args) throws IOException {
    ApplicationContext ctx = SpringApplication.run(App.class, args);
    ctx.getBean(CanvasDataLoader.class).run();
  }
  
  @Bean
  public MatthewsClient matthewsClient(RestTemplate restTemplate) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.add("X-Requested-With", "XMLHttpRequest");
    
    return 
        new MatthewsClient
        .Builder()
        .withBaseUrl(matthewsBaseUrl)
        .withHttpHeaders(httpHeaders)
        .withKey(matthewsApiKey)
        .withRestTemplate(restTemplate)
        .withSecret(matthewsApiSecret)
        .build();
  }

  @Bean
  public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder.build();
  }

  @Bean
  @Primary
  public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
    ObjectMapper objectMapper = builder.createXmlMapper(false).build();
    objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    return objectMapper;
  }
}
