package premier.premierslaautomate.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import premier.premierslaautomate.Models.ProcessedData;
import premier.premierslaautomate.Models.ProjectSLAMatrix;
import premier.premierslaautomate.DataServices.JiraAPI;

@Configuration
public class AppConfiguration {

    @Bean
    @ConfigurationProperties(prefix = "sla")
    public SlaMeasureProperties slaMeasureProperties(){return new SlaMeasureProperties();}

    @Bean
    @ConfigurationProperties(prefix = "project")
    public ProjectConfiguration projectConfiguration(){return new ProjectConfiguration();}

    @Bean
    @ConfigurationProperties(prefix = "slasbacklog")
    public SLABacklog slasbacklog(){return new SLABacklog();}

    @Bean
    @ConfigurationProperties(prefix = "slasnonbacklog")
    public SLANonBackLog slasnonbacklog(){return new SLANonBackLog();}

    @Bean
    public JiraAPI jiraApi() {return new JiraAPI();}

    @Bean
    public ProcessedData processingData() {return new ProcessedData();}

    @Bean
    public ProjectSLAMatrix projectSLAMatrix() {return new ProjectSLAMatrix();}

    @Bean
    RestTemplate restTemplate(SlaMeasureProperties slaMeasuresProperties){
        RestTemplate restTemplate = new RestTemplate();


        return restTemplate;
    }

    @Bean
    ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return objectMapper;
    }

    @Bean
    RestTemplate restTemplate1(ProjectConfiguration projectConfiguration){
        RestTemplate restTemplate = new RestTemplate();

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        restTemplate.getMessageConverters().clear();
        converter.setObjectMapper(objectMapper());
        restTemplate.getMessageConverters().clear();
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }

}
