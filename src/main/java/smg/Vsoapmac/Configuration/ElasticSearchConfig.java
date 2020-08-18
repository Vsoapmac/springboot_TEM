package smg.Vsoapmac.Configuration;

import org.apache.http.HttpHost;
import org.apache.tika.Tika;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration就相当于Spring配置文件中的<beans />标签，里面可以配置bean
@Configuration
public class ElasticSearchConfig {

    //@Bean可理解为用spring的时候xml里面的<bean>标签
    @Bean
    public RestHighLevelClient restHighLevelClient(){
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost" , 9200 , "http")));
        return restHighLevelClient;
    }

    @Bean
    public Tika tika(){
        return new Tika();
    }
}
