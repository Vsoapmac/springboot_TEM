package smg.Vsoapmac.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import smg.Vsoapmac.bean.city;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
public class indexController {

    /**
     * autowired有4种模式，byName、byType、constructor、autodectect
     * 其中@Autowired注解是使用byType方式的
     */
    @Autowired
    private smg.Vsoapmac.Service.cityService cityService;

    @Autowired
    @Qualifier("restHighLevelClient") //@bean的方法名
    private RestHighLevelClient restHighLevelClient;

    private city city;
    private ObjectMapper objectMapper;
    @GetMapping(value = "/connectVue")
    @ResponseBody
    public String connectVue() throws JsonProcessingException {

        objectMapper = new ObjectMapper();
        city = cityService.findById(1).get();
        return objectMapper.writeValueAsString(city);
    }

    /**
     * 以下是RestHighLevelClient对索引(约等于数据库)的操作
     */
    @GetMapping("/createIndex")
    public void createIndex() throws IOException {
        //1.创建索引请求
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("elasticspring");
        //2.客户端执行该请求
        restHighLevelClient.indices().create(createIndexRequest, RequestOptions.DEFAULT);
    }
    @GetMapping("/getIndex")
    public void getIndex() throws IOException {
        //1.获取索引请求
        GetIndexRequest getIndexRequest = new GetIndexRequest("elasticspring");
        //2.客户端执行该请求
        boolean exists = restHighLevelClient.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        //3.检查是否存在
        System.out.println(exists);
    }
    @GetMapping("/deleteIndex")
    public void deleteIndex() throws IOException {
        //1.删除索引请求
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("elasticspring");
        //2.客户端执行该请求
        AcknowledgedResponse acknowledgedResponse = restHighLevelClient.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
        //3.查看通知
        System.out.println(acknowledgedResponse.isAcknowledged());
    }

    /**
     * 以下是以下是RestHighLevelClient对文档(约等于表)的操作
     */
    @GetMapping("/createDocument")
    public void createDocument() throws IOException {
        //1.创建对象
        city = new city();
        city.setID(1);
        city.setName("USA");
        city.setCountryCode("4332");

        //2.创建请求
        IndexRequest indexRequest = new IndexRequest("elasticspring");
        //此举相当于restful的 put /elasticspring/_doc/city
        indexRequest.id("city");
//        indexRequest.timeout("1s");//设置时间

        //3.将对象装换到JSON后添加到请求中
        objectMapper = new ObjectMapper();
        String jsonObject = objectMapper.writeValueAsString(city);
        indexRequest.source(jsonObject , XContentType.JSON);

        //4.客户端发送请求，获取结果
        IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(indexRequest.toString());

        //批量创造数据
        BulkRequest bulkRequest = new BulkRequest();
        List<city> cityList = cityService.findAll();

        for (int i = 0; i < cityList.size(); i++) {
            bulkRequest.add(indexRequest.source(objectMapper.writeValueAsString(cityList.get(i)) , XContentType.JSON));
        }

        restHighLevelClient.bulk(bulkRequest , RequestOptions.DEFAULT);
    }
    @GetMapping("/getDocument")
    public void getDocument() throws IOException {
        // 1.获取请求
        GetRequest getRequest = new GetRequest("elasticspring");
        getRequest.id("city");

        // 2.客户端发送请求
        boolean exists = restHighLevelClient.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);

        // 3.客户端获取信息
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.toString());
    }
    @GetMapping("/updateDocument")
    public void updateDocument() throws IOException {
        // 1.更新请求
        UpdateRequest updateRequest = new UpdateRequest("elasticspring" , "city");

        // 2.创建更新对象并提交到updateRequest
        city = cityService.findById(1).get();
        System.out.println(city.getName());
        objectMapper = new ObjectMapper();
        updateRequest.doc(objectMapper.writeValueAsString(city) , XContentType.JSON);

        // 3.客户端发送请求并返回结果
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse.toString());
    }
    @GetMapping("/deleteDocument")
    public void deleteDocument() throws IOException {
        // 1.删除请求
        DeleteRequest deleteRequest = new DeleteRequest("elasticspring" , "city");

        // 2.客户端发送请求并返回结果
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.toString());
    }

    /**
     * SearchRequest搜索请求
     * SearchSourceBuilder 条件构造
     * HighlightBuilder构造高亮
     * TermQueryBuilder 精确查询
     * MatchAllQueryBuilder 匹配所有
     *
     * xxxQueryBuilder 对应所有restful命令
     */
    @GetMapping("/searchDocument")
    public void searchDocument() throws IOException {
        // 创建搜索请求
        SearchRequest searchRequest = new SearchRequest("elasticspring");
        // 构建搜索条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 查询条件，QueryBuilders工具实现
        // QueryBuilders.termQuery 精确查询
        // QueryBuilders.matchAllQuery 匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "CHINA");

        searchSourceBuilder.query(termQueryBuilder);
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(searchResponse.getHits().getHits().length);
    }
}
