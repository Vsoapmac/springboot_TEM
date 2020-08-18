package smg.Vsoapmac.controller;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;

@RestController
public class JsoupController {
    //需要解析的网页
    private final String url = "https://www.bilibili.com/";
    private final String fileUrl = "D:\\Gotsomenews\\project\\springboot_TEM\\src\\main\\resources\\application.yml";

    @Autowired
    private Tika tika;

    /**
     * Jsoup的document和js的DOM操作基本一致
     * document.getElementById('id')
     *
     * Jsoup里的方法为static，直接调用即可
     */
    @GetMapping("/connect")
    public void connect() throws IOException {
        //获取网站的地址
        Connection connection = Jsoup.connect(url);
        //获取DOM
        Document document = connection.get();
        System.out.println(document.title());
    }

    /**
     * Tika通过MimeType（MIME是MIME(Multipurpose Internet Mail Extensions)多用途互联网邮件扩展类型。
     * 是设定某种扩展名的文件用一种应用程序来打开的方式类型）来实现对一个文档的具体识别工作，通过Language identifier来识别语言。
     * 根据MimeType和Language identifier的识别结果，选择调用具体的Parser来解析文档。而处理则由ContentHandler接口来完成。
     * 其中parser负责解析具体的文档，当解析到需要进行处理的时候，调用具体的信息处理类中的contentHandler进行解析内容的处理。
     * 解析、处理后得到的结果作为返回的值。
     */
    @GetMapping("/tikaParse")
    public void tikaParse() throws IOException, TikaException {
        File file = new File(fileUrl);//获取文件

        String fileContext = tika.parseToString(file);//将文件内容解析为String
        String detect = tika.detect(file);//解析文件类型

        System.out.println(fileContext);
        System.out.println(detect);
    }
}
