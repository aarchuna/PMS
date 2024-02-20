package premier.premierslaautomate.DataServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Component
public class SlaRestClientService
{


    @Autowired
    private RestTemplate restTemplate;



    public <T> T exchange(String url, ParameterizedTypeReference<T> responseClass)
    {
        RequestEntity requestEntity = RequestEntity.get(url).build();
        return restTemplate.exchange(requestEntity,responseClass).getBody();

    }

    public <T> T exchangeWihCredential(String url, ParameterizedTypeReference<T> responseClass, String userName, String password)
    {
        RequestEntity requestEntity = RequestEntity.get(url).build();
        restTemplate.setInterceptors(List.of(new BasicAuthenticationInterceptor(userName, password)));
        return restTemplate.exchange(requestEntity,responseClass).getBody();
    }

    public <T> T exchangeWihCredential(String url, Class<T> responseClass, String userName, String password)
    {
        RequestEntity requestEntity = RequestEntity.get(url).build();
        restTemplate.setInterceptors(List.of(new BasicAuthenticationInterceptor(userName, password)));
        return restTemplate.exchange(requestEntity,responseClass).getBody();
    }
}