package meetingteam.teamservice.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import meetingteam.teamservice.contraints.AnomalyTypes;
import meetingteam.teamservice.utils.AnomalyUtil;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {
    private final AnomalyConfig anomalyConfig;

    @Bean
    public RestClient getRestClient(RestClient.Builder restClientBuilder) {
        return restClientBuilder
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution)->{
                    if(anomalyConfig.enableDownServices()){
                        for(var downService: anomalyConfig.downServicesList()) {
                            if(request.getURI().toString().contains(downService)) {
                                AnomalyUtil.markAnomalySpan(AnomalyTypes.DOWN_SERVICES);
                                break;
                            }
                        }
                    }
                    
                    return execution.execute(request, body);
                })
                .build();
    }
}
