package smg.Vsoapmac.controller;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class JsoupController {
    //需要解析的网页
    private final String url = "https://www.bilibili.com/";

    @GetMapping("/connect")
    public void connect() throws IOException {
        //获取网站的地址
        Connection connection = Jsoup.connect(url);
        //获取DOM
        Document document = connection.get();

        System.out.println(document.title());
    }
}
