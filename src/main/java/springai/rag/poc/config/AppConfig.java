package springai.rag.poc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static springai.rag.poc.constants.AppConstants.MIN_THREAD_POOL_SIZE;

/**
 * Application Configuration.
 */
@Configuration
public class AppConfig {

    /**
     * Configures an instance of `Executor Service`
     * for parallel processing.
     * @return - ExecutorService {@link  ExecutorService}
     */
    @Bean
    public ExecutorService executorService(){
        return Executors.newFixedThreadPool(MIN_THREAD_POOL_SIZE);
    }


    /**
     * configures an instance of `RestTemplate`
     * Rest client to call external api's
     * @return RestTemplate - {@link RestTemplate}
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
