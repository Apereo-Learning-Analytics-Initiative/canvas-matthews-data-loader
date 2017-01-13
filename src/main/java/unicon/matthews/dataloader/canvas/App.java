package unicon.matthews.dataloader.canvas;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
  
  @Value("${canvas.apikey}")
  private String canvasApiKey;
  
  @Value("${canvas.apisecret}")
  private String canvasApiSecret;
  
  @Value("${canvas.baseurl:portal.inshosteddata.com}")
  private String canvasBaseUrl;

  public static void main(String[] args) throws IOException {
    ApplicationContext ctx = SpringApplication.run(App.class, args);
    ctx.getBean(CanvasDataLoader.class).run();
  }
  
  @Bean
  public MatthewsClient matthewsClient() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.add("X-Requested-With", "XMLHttpRequest");
    
    return 
        new MatthewsClient
        .Builder()
        .withBaseUrl(matthewsBaseUrl)
        .withHttpHeaders(httpHeaders)
        .withKey(matthewsApiKey)
        .withRestTemplate(new RestTemplate())
        .withSecret(matthewsApiSecret)
        .build();
  }

  @Bean
  public ApiClient apiClient() {
    return new ApiClient(canvasBaseUrl, canvasApiKey, canvasApiSecret);
  }
}
